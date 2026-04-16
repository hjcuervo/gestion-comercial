package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago.EstadoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoRepository;
import com.arquitecsoft.gestion.domain.facturacion.dto.EmisionPendienteResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcFormaPagoGestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmisionPendienteService {

    private final GcContratoRepository contratoRepository;
    private final GcFormaPagoGestionRepository gestionRepository;
    private final TrmService trmService;

    public EmisionPendienteService(GcContratoRepository contratoRepository,
                                    GcFormaPagoGestionRepository gestionRepository,
                                    TrmService trmService) {
        this.contratoRepository = contratoRepository;
        this.gestionRepository = gestionRepository;
        this.trmService = trmService;
    }

    /**
     * Obtiene todas las formas de pago pendientes de contratos VIGENTES,
     * con datos del contrato/empresa y conversión a COP.
     * Solo contratos VIGENTES (RN del Mundo 3).
     */
    @Transactional(readOnly = true)
    public List<EmisionPendienteResponse> obtenerPendientes(Integer anio, Integer mes, String estado) {
        // Cargar contratos vigentes con formas de pago en un solo query
        List<GcContrato> contratos = contratoRepository.findByEstadoWithFormasPago(GcContrato.EstadoContrato.VIGENTE);

        List<EmisionPendienteResponse> resultado = new ArrayList<>();

        for (GcContrato contrato : contratos) {
            if (contrato.getFormasPago() == null) continue;

            for (GcContratoFormaPago fp : contrato.getFormasPago()) {
                // Filtrar por estado si se especifica
                if ("PENDIENTE".equals(estado) && fp.getEstado() != EstadoFormaPago.PENDIENTE) continue;
                if ("FACTURADA".equals(estado) && fp.getEstado() != EstadoFormaPago.FACTURADA) continue;
                if (estado == null && fp.getEstado() != EstadoFormaPago.PENDIENTE) continue;

                // Extraer año-mes de la fecha estimada
                int fpAnio = 0, fpMes = 0;
                if (fp.getFechaEstimadaPago() != null) {
                    fpAnio = fp.getFechaEstimadaPago().getYear();
                    fpMes = fp.getFechaEstimadaPago().getMonthValue();
                }

                // Filtrar por año-mes si se especifica
                if (anio != null && fpAnio != anio) continue;
                if (mes != null && fpMes != mes) continue;

                EmisionPendienteResponse item = new EmisionPendienteResponse();
                item.setFormaPagoId(fp.getId());
                item.setContratoId(contrato.getId());
                item.setContratoNumeroInterno(contrato.getNumeroContratoInterno());
                item.setContratoCliente(contrato.getNumeroContratoCliente());
                item.setTipoContrato(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getNombre() : null);
                item.setEmpresaId(contrato.getEmpresa().getId());
                item.setEmpresaNombre(contrato.getEmpresa().getRazonSocial());
                item.setEmpresaIdentificacion(contrato.getEmpresa().getIdentificacionTributaria());
                item.setFormaPagoDescripcion(fp.getDescripcion());
                item.setValor(fp.getValor());
                item.setValorFacturado(fp.getValorFacturado());
                item.setMoneda(contrato.getMoneda());
                item.setFechaEstimadaPago(fp.getFechaEstimadaPago());
                item.setAnio(fpAnio);
                item.setMes(fpMes);
                item.setEstado(fp.getEstado().name());
                item.setVencida(fp.isVencida());

                // Conversión a COP
                BigDecimal valorParaCop = fp.getValorFacturado() != null ? fp.getValorFacturado() : fp.getValor();
                if ("USD".equals(contrato.getMoneda()) && valorParaCop != null && fpAnio > 0) {
                    item.setValorCop(trmService.convertirACop(valorParaCop, fpAnio, fpMes));
                } else {
                    item.setValorCop(valorParaCop);
                }

                // Última gestión
                GcFormaPagoGestion ultimaGestion = gestionRepository.findLastByFormaPagoId(fp.getId());
                if (ultimaGestion != null) {
                    item.setUltimaGestionTipo(ultimaGestion.getTipoGestion().name());
                    item.setUltimaGestionFecha(ultimaGestion.getFechaGestion());
                }

                resultado.add(item);
            }
        }

        // Ordenar: vencidas primero, luego por fecha
        resultado.sort((a, b) -> {
            if (a.isVencida() && !b.isVencida()) return -1;
            if (!a.isVencida() && b.isVencida()) return 1;
            if (a.getFechaEstimadaPago() == null) return 1;
            if (b.getFechaEstimadaPago() == null) return -1;
            return a.getFechaEstimadaPago().compareTo(b.getFechaEstimadaPago());
        });

        return resultado;
    }

    /**
     * KPIs para el tablero de facturación.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerKpis() {
        LocalDate hoy = LocalDate.now();
        int mesActual = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        List<EmisionPendienteResponse> pendientes = obtenerPendientes(null, null, "PENDIENTE");
        List<EmisionPendienteResponse> facturadas = obtenerPendientes(null, null, "FACTURADA");

        // Pendientes del mes actual
        List<EmisionPendienteResponse> pendientesMes = pendientes.stream()
                .filter(p -> p.getAnio() == anioActual && p.getMes() == mesActual)
                .collect(Collectors.toList());

        // Vencidas (meses anteriores)
        List<EmisionPendienteResponse> vencidas = pendientes.stream()
                .filter(EmisionPendienteResponse::isVencida)
                .collect(Collectors.toList());

        // Facturadas este mes (por fecha de la forma de pago)
        List<EmisionPendienteResponse> facturadasMes = facturadas.stream()
                .filter(f -> f.getAnio() == anioActual && f.getMes() == mesActual)
                .collect(Collectors.toList());

        // Facturadas del año
        List<EmisionPendienteResponse> facturadasAnio = facturadas.stream()
                .filter(f -> f.getAnio() == anioActual)
                .collect(Collectors.toList());

        Map<String, Object> kpis = new LinkedHashMap<>();
        kpis.put("pendientesMesCantidad", pendientesMes.size());
        kpis.put("pendientesMesValorCop", sumarValorCop(pendientesMes));
        kpis.put("vencidasCantidad", vencidas.size());
        kpis.put("vencidasValorCop", sumarValorCop(vencidas));
        kpis.put("facturadasMesCantidad", facturadasMes.size());
        kpis.put("facturadasMesValorCop", sumarValorFacturadoCop(facturadasMes));
        kpis.put("pendienteTotalCantidad", pendientes.size());
        kpis.put("pendienteTotalValorCop", sumarValorCop(pendientes));
        kpis.put("facturadasAnioCantidad", facturadasAnio.size());
        kpis.put("facturadasAnioValorCop", sumarValorFacturadoCop(facturadasAnio));

        return kpis;
    }

    private BigDecimal sumarValorCop(List<EmisionPendienteResponse> items) {
        return items.stream()
                .map(i -> i.getValorCop() != null ? i.getValorCop() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumarValorFacturadoCop(List<EmisionPendienteResponse> items) {
        return items.stream()
                .map(i -> {
                    BigDecimal val = i.getValorFacturado() != null ? i.getValorFacturado() : i.getValor();
                    if ("USD".equals(i.getMoneda()) && i.getAnio() > 0) {
                        BigDecimal cop = trmService.convertirACop(val, i.getAnio(), i.getMes());
                        return cop != null ? cop : BigDecimal.ZERO;
                    }
                    return val != null ? val : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
