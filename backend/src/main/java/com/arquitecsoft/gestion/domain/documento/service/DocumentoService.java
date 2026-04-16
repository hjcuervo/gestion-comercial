package com.arquitecsoft.gestion.domain.documento.service;

import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;
import com.arquitecsoft.gestion.domain.actividad.repository.GcActividadRepository;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoModificacion;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoModificacionRepository;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoRepository;
import com.arquitecsoft.gestion.domain.documento.dto.DocumentoCreateRequest;
import com.arquitecsoft.gestion.domain.documento.dto.DocumentoResponse;
import com.arquitecsoft.gestion.domain.documento.entity.GcDocumento;
import com.arquitecsoft.gestion.domain.documento.repository.GcDocumentoRepository;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentoService {

    private final GcDocumentoRepository documentoRepository;
    private final GcOportunidadRepository oportunidadRepository;
    private final GcActividadRepository actividadRepository;
    private final GcContratoRepository contratoRepository;
    private final GcContratoModificacionRepository modificacionRepository;
    private final SecurityUtils securityUtils;

    public DocumentoService(GcDocumentoRepository documentoRepository,
                            GcOportunidadRepository oportunidadRepository,
                            GcActividadRepository actividadRepository,
                            GcContratoRepository contratoRepository,
                            GcContratoModificacionRepository modificacionRepository,
                            SecurityUtils securityUtils) {
        this.documentoRepository = documentoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.actividadRepository = actividadRepository;
        this.contratoRepository = contratoRepository;
        this.modificacionRepository = modificacionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentoResponse> listar(Long oportunidadId, Long actividadId,
                                                   Long tipoDocumentoId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("fechaCarga").descending());
        Page<GcDocumento> pageResult = documentoRepository.findWithFilters(oportunidadId, actividadId, tipoDocumentoId, pageable);
        return PageResponse.from(pageResult, DocumentoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorOportunidad(Long oportunidadId) {
        if (!oportunidadRepository.existsById(oportunidadId))
            throw new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + oportunidadId);
        return documentoRepository.findByOportunidadId(oportunidadId).stream()
                .map(DocumentoResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorActividad(Long actividadId) {
        if (!actividadRepository.existsById(actividadId))
            throw new BusinessException("NOT_FOUND", "Actividad no encontrada con ID: " + actividadId);
        return documentoRepository.findByActividadId(actividadId).stream()
                .map(DocumentoResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorContrato(Long contratoId) {
        if (!contratoRepository.existsById(contratoId))
            throw new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + contratoId);
        return documentoRepository.findByContratoId(contratoId).stream()
                .map(DocumentoResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorModificacion(Long modificacionId) {
        if (!modificacionRepository.existsById(modificacionId))
            throw new BusinessException("NOT_FOUND", "Modificación no encontrada con ID: " + modificacionId);
        return documentoRepository.findByModificacionId(modificacionId).stream()
                .map(DocumentoResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoResponse obtenerPorId(Long id) {
        GcDocumento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Documento no encontrado con ID: " + id));
        return DocumentoResponse.fromEntity(documento);
    }

    @Transactional
    public DocumentoResponse crear(DocumentoCreateRequest request) {
        if (request.getOportunidadId() == null && request.getActividadId() == null
                && request.getContratoId() == null && request.getModificacionId() == null) {
            throw new BusinessException("VALIDATION_ERROR",
                "Debe especificar oportunidadId, actividadId, contratoId o modificacionId");
        }

        GcDocumento documento = new GcDocumento();

        if (request.getOportunidadId() != null) {
            GcOportunidad oportunidad = oportunidadRepository.findById(request.getOportunidadId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada"));
            documento.setOportunidad(oportunidad);
        }
        if (request.getActividadId() != null) {
            GcActividad actividad = actividadRepository.findById(request.getActividadId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Actividad no encontrada"));
            documento.setActividad(actividad);
        }
        if (request.getContratoId() != null) {
            GcContrato contrato = contratoRepository.findById(request.getContratoId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado"));
            documento.setContrato(contrato);
        }
        if (request.getModificacionId() != null) {
            GcContratoModificacion modificacion = modificacionRepository.findById(request.getModificacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Modificación no encontrada"));
            documento.setModificacion(modificacion);
        }

        documento.setTipoDocumentoId(request.getTipoDocumentoId());
        documento.setNombre(request.getNombre());

        if (StringUtils.hasText(request.getUrl())) {
            documento.setUrlStorage(request.getUrl());
            documento.setNombreOriginal(request.getNombre());
            documento.setExtension("url");
            documento.setTamanoBytes(0L);
            documento.setBucketName("enlace");
            documento.setObjectKey("enlace/" + request.getNombre());
        } else {
            documento.setUrlStorage(request.getUrlStorage());
            documento.setNombreOriginal(request.getNombreOriginal() != null ? request.getNombreOriginal() : request.getNombre());
            documento.setExtension(request.getExtension() != null ? request.getExtension() : "doc");
            documento.setTamanoBytes(request.getTamanoBytes() != null ? request.getTamanoBytes() : 0L);
            documento.setBucketName(request.getBucketName() != null ? request.getBucketName() : "default");
            documento.setObjectKey(request.getObjectKey() != null ? request.getObjectKey() : "docs/" + request.getNombre());
        }

        documento.setCargadoPor(securityUtils.getCurrentUserId());
        documento = documentoRepository.save(documento);
        return DocumentoResponse.fromEntity(documento);
    }

    @Transactional
    public void eliminar(Long id) {
        GcDocumento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Documento no encontrado con ID: " + id));
        documentoRepository.delete(documento);
    }
}
