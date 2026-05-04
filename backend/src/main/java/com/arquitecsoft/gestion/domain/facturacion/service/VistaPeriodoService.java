package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.facturacion.dto.CompromisoPeriodoItem;
import com.arquitecsoft.gestion.domain.facturacion.dto.KpisPeriodoData;
import com.arquitecsoft.gestion.domain.facturacion.dto.VistaPeriodoResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcTrm;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcCompromisoIngresoRepository;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcTrmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de la vista agregada por periodo (dashboard de compromisos pendientes).
 *
 * Resuelve el año-mes objetivo (parámetro explícito o mes actual del sistema),
 * filtra compromisos con saldoPendiente > 0 hasta el último día del mes,
 * particiona en 3 grupos según fechaEsperadaActual, y agrega KPIs en COP
 * usando TRM del año-mes de cada compromiso.
 */
@Service
public class VistaPeriodoService {

    private static final String COP = "COP";
    private static final String KEY_ARRASTRE = "arrastreMesesAnteriores";
    private static final String KEY_VENCIDOS = "mesActualVencidos";
    private static final String KEY_POR_VENCER = "mesActualPorVencer";

    private final GcCompromisoIngresoRepository compromisoRepo;
    private final GcTrmRepository trmRepo;

    public VistaPeriodoService(GcCompromisoIngresoRepository compromisoRepo,
                                GcTrmRepository trmRepo) {
        this.compromisoRepo = compromisoRepo;
        this.trmRepo = trmRepo;
    }

    @Transactional(readOnly = true)
    public VistaPeriodoResponse obtenerVista(YearMonth anioMes, Long contratoId, Long empresaId) {
        YearMonth objetivo = anioMes != null ? anioMes : YearMonth.now();
        YearMonth actual = YearMonth.now();
        LocalDate hoy = LocalDate.now();
        boolean esMesActual = objetivo.equals(actual);

        LocalDate primerDiaMes = objetivo.atDay(1);
        LocalDate ultimoDiaMes = objetivo.atEndOfMonth();

        // Query: pendientes (saldoPendiente > 0) cuya fecha esperada <= último
        // día del mes objetivo. Filtros opcionales por contrato/empresa.
        List<GcCompromisoIngreso> todos = compromisoRepo.findPendientesHastaFecha(
                ultimoDiaMes, contratoId, empresaId);

        // Mapa de TRMs ya resueltas por año-mes (evita repetir queries a BD)
        Map<YearMonth, BigDecimal> trmCache = new HashMap<>();
        Set<YearMonth> trmFaltantes = new HashSet<>();

        // Particionar en 3 grupos y convertir cada item
        List<CompromisoPeriodoItem> arrastre = new ArrayList<>();
        List<CompromisoPeriodoItem> vencidos = new ArrayList<>();
        List<CompromisoPeriodoItem> porVencer = new ArrayList<>();

        for (GcCompromisoIngreso c : todos) {
            LocalDate fecha = c.getFechaEsperadaActual();
            CompromisoPeriodoItem item = toItem(c, hoy, trmCache, trmFaltantes);

            if (fecha.isBefore(primerDiaMes)) {
                arrastre.add(item);
            } else if (!fecha.isAfter(ultimoDiaMes)) {
                // Está en el mes objetivo
                if (esMesActual) {
                    if (!fecha.isAfter(hoy)) {
                        vencidos.add(item);
                    } else {
                        porVencer.add(item);
                    }
                } else if (objetivo.isBefore(actual)) {
                    // Mes pasado consultado — todos cuentan como arrastre
                    // (en ese momento habrían sido "del mes", pero al ver
                    // desde hoy ya están todos vencidos)
                    arrastre.add(item);
                } else {
                    // Mes futuro — todos por vencer
                    porVencer.add(item);
                }
            }
        }

        // Ordenar cada grupo por fecha ascendente
        Comparator<CompromisoPeriodoItem> byFecha = Comparator.comparing(CompromisoPeriodoItem::getFechaEsperadaActual);
        arrastre.sort(byFecha);
        vencidos.sort(byFecha);
        porVencer.sort(byFecha);

        // Agregar KPIs
        KpisPeriodoData kpis = calcularKpis(arrastre, vencidos, porVencer);

        VistaPeriodoResponse resp = new VistaPeriodoResponse();
        resp.setAnioMes(VistaPeriodoResponse.toAnioMes(objetivo));
        resp.setFechaReferencia(hoy);
        resp.setEsMesActual(esMesActual);
        resp.setMoneda(COP);
        resp.setKpis(kpis);
        resp.setArrastreMesesAnteriores(arrastre);
        resp.setMesActualVencidos(vencidos);
        resp.setMesActualPorVencer(porVencer);

        // Nota si alguna TRM tuvo que aproximarse con la más reciente anterior
        if (!trmFaltantes.isEmpty()) {
            resp.setNotaTrm("Algunas conversiones usaron TRM aproximada: " + trmFaltantes);
        }

        return resp;
    }

    // ---------- Conversión TRM ----------

    /**
     * Obtiene TRM de MON→COP para un año-mes. Estrategia:
     *  1. Busca TRM exacta del año-mes
     *  2. Si no existe, busca la más reciente anterior (findMostRecentBefore)
     *  3. Si tampoco existe, devuelve null (la conversión omite el item)
     *
     * El set trmFaltantes registra los año-mes donde hubo que aproximar.
     */
    private BigDecimal obtenerTrm(String monedaOrigen, YearMonth anioMes,
                                   Map<YearMonth, BigDecimal> cache,
                                   Set<YearMonth> faltantes) {
        if (COP.equalsIgnoreCase(monedaOrigen)) {
            return BigDecimal.ONE;
        }
        if (cache.containsKey(anioMes)) {
            return cache.get(anioMes);
        }
        List<GcTrm> trms = trmRepo.findMostRecentBefore(
                anioMes.getYear(), anioMes.getMonthValue(),
                monedaOrigen, COP);
        if (trms == null || trms.isEmpty()) {
            cache.put(anioMes, null);
            return null;
        }
        GcTrm trm = trms.get(0);
        BigDecimal valor = extraerValorTrm(trm);

        // Si no coincide exactamente con el año-mes solicitado, registramos
        // que hubo aproximación.
        if (trm.getAnio() != anioMes.getYear() || trm.getMes() != anioMes.getMonthValue()) {
            faltantes.add(anioMes);
        }
        cache.put(anioMes, valor);
        return valor;
    }

    /**
     * Extrae el valor de la TRM tolerando variaciones en el nombre del getter.
     * Si tu entidad GcTrm usa un nombre distinto (p.ej. getTrm, getTasa),
     * ajusta este método.
     */
    private BigDecimal extraerValorTrm(GcTrm trm) {
        try {
            return (BigDecimal) trm.getClass().getMethod("getValor").invoke(trm);
        } catch (Exception ignore) { /* intenta siguientes */ }
        try {
            return (BigDecimal) trm.getClass().getMethod("getTrm").invoke(trm);
        } catch (Exception ignore) { /* intenta siguientes */ }
        try {
            return (BigDecimal) trm.getClass().getMethod("getTasa").invoke(trm);
        } catch (Exception ignore) { /* intenta siguientes */ }
        throw new IllegalStateException(
            "GcTrm no expone getValor()/getTrm()/getTasa() — ajustar extraerValorTrm()");
    }

    // ---------- Mapeo entidad → DTO ----------

    private CompromisoPeriodoItem toItem(GcCompromisoIngreso c, LocalDate hoy,
                                          Map<YearMonth, BigDecimal> cacheTrm,
                                          Set<YearMonth> faltantes) {
        CompromisoPeriodoItem item = new CompromisoPeriodoItem();
        item.setId(c.getId());

        if (c.getContrato() != null) {
            item.setContratoId(c.getContrato().getId());
            item.setContratoNumero(c.getContrato().getNumeroContratoInterno());
            if (c.getContrato().getEmpresa() != null) {
                item.setEmpresaId(c.getContrato().getEmpresa().getId());
                item.setEmpresaNombre(c.getContrato().getEmpresa().getRazonSocial());
            }
        }
        item.setDescripcion(c.getDescripcion());
        item.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        item.setMoneda(c.getMoneda());
        item.setFechaEsperadaActual(c.getFechaEsperadaActual());

        BigDecimal presu = nz(c.getMontoPresupuestado());
        BigDecimal fact = nz(c.getMontoFacturadoAcumulado());
        BigDecimal saldo = presu.subtract(fact);

        item.setMontoPresupuestado(presu);
        item.setMontoFacturadoAcumulado(fact);
        item.setSaldoPendiente(saldo);

        // Conversión a COP
        YearMonth ymFecha = YearMonth.from(c.getFechaEsperadaActual());
        BigDecimal trm = obtenerTrm(c.getMoneda(), ymFecha, cacheTrm, faltantes);
        if (trm != null) {
            item.setTrmAplicada(COP.equalsIgnoreCase(c.getMoneda()) ? null : trm);
            item.setMontoPresupuestadoCOP(presu.multiply(trm).setScale(2, RoundingMode.HALF_UP));
            item.setMontoFacturadoAcumuladoCOP(fact.multiply(trm).setScale(2, RoundingMode.HALF_UP));
            item.setSaldoPendienteCOP(saldo.multiply(trm).setScale(2, RoundingMode.HALF_UP));
        } else {
            // Sin TRM: no convertimos — el frontend puede mostrar guión
            item.setMontoPresupuestadoCOP(null);
            item.setMontoFacturadoAcumuladoCOP(null);
            item.setSaldoPendienteCOP(null);
        }

        item.setDiasVencimiento((int) ChronoUnit.DAYS.between(hoy, c.getFechaEsperadaActual()));
        return item;
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ---------- Cálculo de KPIs ----------

    private KpisPeriodoData calcularKpis(List<CompromisoPeriodoItem> arrastre,
                                          List<CompromisoPeriodoItem> vencidos,
                                          List<CompromisoPeriodoItem> porVencer) {
        KpisPeriodoData k = new KpisPeriodoData();

        // Totales sobre la unión de los 3 grupos
        List<CompromisoPeriodoItem> todos = new ArrayList<>(arrastre.size() + vencidos.size() + porVencer.size());
        todos.addAll(arrastre);
        todos.addAll(vencidos);
        todos.addAll(porVencer);

        BigDecimal presu = todos.stream()
                .map(i -> nz(i.getMontoPresupuestadoCOP()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal fact = todos.stream()
                .map(i -> nz(i.getMontoFacturadoAcumuladoCOP()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = todos.stream()
                .map(i -> nz(i.getSaldoPendienteCOP()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        k.setTotalPresupuestadoCOP(presu);
        k.setTotalFacturadoCOP(fact);
        k.setTotalSaldoPendienteCOP(saldo);

        if (presu.compareTo(BigDecimal.ZERO) > 0) {
            k.setPorcentajeCumplimiento(fact.divide(presu, 4, RoundingMode.HALF_UP));
        }

        // Conteos por grupo
        k.getConteosPorGrupo().put(KEY_ARRASTRE, arrastre.size());
        k.getConteosPorGrupo().put(KEY_VENCIDOS, vencidos.size());
        k.getConteosPorGrupo().put(KEY_POR_VENCER, porVencer.size());

        // Montos por grupo (saldo pendiente)
        k.getMontosPorGrupoCOP().put(KEY_ARRASTRE, sumarSaldo(arrastre));
        k.getMontosPorGrupoCOP().put(KEY_VENCIDOS, sumarSaldo(vencidos));
        k.getMontosPorGrupoCOP().put(KEY_POR_VENCER, sumarSaldo(porVencer));

        // Distribución por estado (ordenada por nombre del estado)
        Map<String, Long> byEstado = todos.stream()
                .collect(Collectors.groupingBy(CompromisoPeriodoItem::getEstado, Collectors.counting()));
        byEstado.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> k.getDistribucionEstado().put(e.getKey(), e.getValue().intValue()));

        return k;
    }

    private BigDecimal sumarSaldo(List<CompromisoPeriodoItem> items) {
        return items.stream()
                .map(i -> nz(i.getSaldoPendienteCOP()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
