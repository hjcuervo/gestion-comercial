package com.arquitecsoft.gestion.domain.oportunidad.service;

import com.arquitecsoft.gestion.domain.oportunidad.dto.OportunidadStatsResponse;
import com.arquitecsoft.gestion.domain.oportunidad.dto.OportunidadStatsResponse.*;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad.EstadoMacro;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OportunidadStatsService {

    private final GcOportunidadRepository oportunidadRepository;

    private static final String[] MESES_ES = {
        "", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    public OportunidadStatsService(GcOportunidadRepository oportunidadRepository) {
        this.oportunidadRepository = oportunidadRepository;
    }

    @Transactional(readOnly = true)
    public OportunidadStatsResponse obtenerEstadisticas(Long pipelineId, Integer meses) {
        if (meses == null || meses <= 0) meses = 12;

        List<GcOportunidad> todas;

        // Filtrar por pipeline si se especifica
        if (pipelineId != null) {
            todas = oportunidadRepository.findAllWithRelationsByPipeline(pipelineId);
        } else {
            todas = oportunidadRepository.findAllWithRelations();
        }

        OportunidadStatsResponse stats = new OportunidadStatsResponse();

        // === KPIs principales ===
        long abiertas = todas.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.ABIERTA || o.getEstadoMacro() == EstadoMacro.SEGUIMIENTO).count();
        long ganadas = todas.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.GANADA).count();
        long perdidas = todas.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.PERDIDA).count();
        long noConcretadas = todas.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.NO_CONCRETADA).count();

        BigDecimal valorPipeline = todas.stream()
                .filter(o -> o.getEstadoMacro() == EstadoMacro.ABIERTA || o.getEstadoMacro() == EstadoMacro.SEGUIMIENTO)
                .map(o -> o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorGanado = todas.stream()
                .filter(o -> o.getEstadoMacro() == EstadoMacro.GANADA)
                .map(o -> o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorPerdido = todas.stream()
                .filter(o -> o.getEstadoMacro() == EstadoMacro.PERDIDA)
                .map(o -> o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cerradas = ganadas + perdidas + noConcretadas;
        double tasaConversion = cerradas > 0 ? (double) ganadas / cerradas * 100 : 0;

        stats.setTotalAbiertas(abiertas);
        stats.setTotalGanadas(ganadas);
        stats.setTotalPerdidas(perdidas);
        stats.setTotalNoConcretadas(noConcretadas);
        stats.setValorTotalPipeline(valorPipeline);
        stats.setValorTotalGanado(valorGanado);
        stats.setValorTotalPerdido(valorPerdido);
        stats.setTasaConversion(Math.round(tasaConversion * 10.0) / 10.0);

        // === Por etapa (funnel) - solo abiertas/seguimiento ===
        Map<Long, List<GcOportunidad>> porEtapaMap = todas.stream()
                .filter(o -> o.getEstadoMacro() == EstadoMacro.ABIERTA || o.getEstadoMacro() == EstadoMacro.SEGUIMIENTO)
                .collect(Collectors.groupingBy(o -> o.getEtapa().getId()));

        List<EtapaStat> etapaStats = new ArrayList<>();
        porEtapaMap.forEach((etapaId, ops) -> {
            GcOportunidad sample = ops.get(0);
            BigDecimal valor = ops.stream()
                    .map(o -> o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            etapaStats.add(new EtapaStat(
                    etapaId,
                    sample.getEtapa().getNombre(),
                    sample.getEtapa().getColor(),
                    sample.getEtapa().getOrden(),
                    ops.size(),
                    valor
            ));
        });
        etapaStats.sort(Comparator.comparingInt(e -> e.getEtapaOrden() != null ? e.getEtapaOrden() : 0));
        stats.setPorEtapa(etapaStats);

        // === Por mes (últimos N meses) ===
        LocalDateTime desde = LocalDateTime.now().minusMonths(meses).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        DateTimeFormatter fmtKey = DateTimeFormatter.ofPattern("yyyy-MM");

        // Generar todos los meses del rango
        Map<String, MesStat> mesMap = new LinkedHashMap<>();
        for (int i = 0; i < meses; i++) {
            LocalDateTime m = LocalDateTime.now().minusMonths(meses - 1 - i);
            String key = m.format(fmtKey);
            MesStat ms = new MesStat();
            ms.setMes(key);
            ms.setMesLabel(MESES_ES[m.getMonthValue()] + " " + m.getYear());
            ms.setNuevas(0);
            ms.setGanadas(0);
            ms.setPerdidas(0);
            ms.setValorNuevas(BigDecimal.ZERO);
            ms.setValorGanado(BigDecimal.ZERO);
            mesMap.put(key, ms);
        }

        // Contar nuevas por fecha de creación
        todas.stream()
                .filter(o -> o.getFechaCreacion() != null && o.getFechaCreacion().isAfter(desde))
                .forEach(o -> {
                    String key = o.getFechaCreacion().format(fmtKey);
                    MesStat ms = mesMap.get(key);
                    if (ms != null) {
                        ms.setNuevas(ms.getNuevas() + 1);
                        ms.setValorNuevas(ms.getValorNuevas().add(
                                o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO));
                    }
                });

        // Contar ganadas/perdidas por fecha de cierre
        todas.stream()
                .filter(o -> o.getFechaCierre() != null && o.getFechaCierre().isAfter(desde))
                .forEach(o -> {
                    String key = o.getFechaCierre().format(fmtKey);
                    MesStat ms = mesMap.get(key);
                    if (ms != null) {
                        if (o.getEstadoMacro() == EstadoMacro.GANADA) {
                            ms.setGanadas(ms.getGanadas() + 1);
                            ms.setValorGanado(ms.getValorGanado().add(
                                    o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO));
                        } else if (o.getEstadoMacro() == EstadoMacro.PERDIDA) {
                            ms.setPerdidas(ms.getPerdidas() + 1);
                        }
                    }
                });

        stats.setPorMes(new ArrayList<>(mesMap.values()));

        // === Top 10 oportunidades por valor (abiertas/seguimiento) ===
        List<TopOportunidad> top = todas.stream()
                .filter(o -> o.getEstadoMacro() == EstadoMacro.ABIERTA || o.getEstadoMacro() == EstadoMacro.SEGUIMIENTO)
                .filter(o -> o.getValorEstimado() != null)
                .sorted((a, b) -> b.getValorEstimado().compareTo(a.getValorEstimado()))
                .limit(10)
                .map(o -> {
                    TopOportunidad t = new TopOportunidad();
                    t.setId(o.getId());
                    t.setNombre(o.getNombre());
                    t.setEmpresaNombre(o.getEmpresa().getRazonSocial());
                    t.setEtapaNombre(o.getEtapa().getNombre());
                    t.setEstadoMacro(o.getEstadoMacro().name());
                    t.setValorEstimado(o.getValorEstimado());
                    t.setMoneda(o.getMoneda());
                    t.setProbabilidad(o.getProbabilidad());
                    return t;
                })
                .collect(Collectors.toList());
        stats.setTopOportunidades(top);

        // === Por empresa (top 10) ===
        Map<Long, List<GcOportunidad>> porEmpresaMap = todas.stream()
                .collect(Collectors.groupingBy(o -> o.getEmpresa().getId()));

        List<EmpresaStat> empresaStats = porEmpresaMap.entrySet().stream()
                .map(entry -> {
                    List<GcOportunidad> ops = entry.getValue();
                    GcOportunidad sample = ops.get(0);
                    EmpresaStat es = new EmpresaStat();
                    es.setEmpresaId(entry.getKey());
                    es.setEmpresaNombre(sample.getEmpresa().getRazonSocial());
                    es.setTotalOportunidades(ops.size());
                    es.setAbiertas(ops.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.ABIERTA || o.getEstadoMacro() == EstadoMacro.SEGUIMIENTO).count());
                    es.setGanadas(ops.stream().filter(o -> o.getEstadoMacro() == EstadoMacro.GANADA).count());
                    es.setValorTotal(ops.stream()
                            .map(o -> o.getValorEstimado() != null ? o.getValorEstimado() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return es;
                })
                .sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal()))
                .limit(10)
                .collect(Collectors.toList());
        stats.setPorEmpresa(empresaStats);

        return stats;
    }
}
