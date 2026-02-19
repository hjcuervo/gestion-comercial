package com.arquitecsoft.gestion.domain.documento.service;

import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;
import com.arquitecsoft.gestion.domain.actividad.repository.GcActividadRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentoService {

    private final GcDocumentoRepository documentoRepository;
    private final GcOportunidadRepository oportunidadRepository;
    private final GcActividadRepository actividadRepository;
    private final SecurityUtils securityUtils;

    public DocumentoService(GcDocumentoRepository documentoRepository,
                            GcOportunidadRepository oportunidadRepository,
                            GcActividadRepository actividadRepository,
                            SecurityUtils securityUtils) {
        this.documentoRepository = documentoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.actividadRepository = actividadRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentoResponse> listar(Long oportunidadId, Long actividadId, 
                                                   Long tipoDocumentoId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("fechaCarga").descending());

        Page<GcDocumento> pageResult = documentoRepository.findWithFilters(
                oportunidadId, actividadId, tipoDocumentoId, pageable);

        return PageResponse.from(pageResult, DocumentoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorOportunidad(Long oportunidadId) {
        // Verificar que la oportunidad existe
        if (!oportunidadRepository.existsById(oportunidadId)) {
            throw new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + oportunidadId);
        }

        return documentoRepository.findByOportunidadId(oportunidadId).stream()
                .map(DocumentoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorActividad(Long actividadId) {
        // Verificar que la actividad existe
        if (!actividadRepository.existsById(actividadId)) {
            throw new BusinessException("NOT_FOUND", "Actividad no encontrada con ID: " + actividadId);
        }

        return documentoRepository.findByActividadId(actividadId).stream()
                .map(DocumentoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoResponse obtenerPorId(Long id) {
        GcDocumento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Documento no encontrado con ID: " + id));

        return DocumentoResponse.fromEntity(documento);
    }

    @Transactional
    public DocumentoResponse crear(DocumentoCreateRequest request) {
        // Validar que al menos uno de oportunidadId o actividadId viene
        if (request.getOportunidadId() == null && request.getActividadId() == null) {
            throw new BusinessException("VALIDATION_ERROR", 
                "Debe especificar oportunidadId o actividadId");
        }

        GcDocumento documento = new GcDocumento();

        // Asociar a oportunidad si viene
        if (request.getOportunidadId() != null) {
            GcOportunidad oportunidad = oportunidadRepository.findById(request.getOportunidadId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", 
                        "Oportunidad no encontrada con ID: " + request.getOportunidadId()));
            documento.setOportunidad(oportunidad);
        }

        // Asociar a actividad si viene
        if (request.getActividadId() != null) {
            GcActividad actividad = actividadRepository.findById(request.getActividadId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", 
                        "Actividad no encontrada con ID: " + request.getActividadId()));
            documento.setActividad(actividad);
        }

        documento.setTipoDocumentoId(request.getTipoDocumentoId());
        documento.setNombre(request.getNombre());
        documento.setNombreOriginal(request.getNombreOriginal());
        documento.setExtension(request.getExtension());
        documento.setTamanoBytes(request.getTamanoBytes());
        documento.setUrlStorage(request.getUrlStorage());
        documento.setBucketName(request.getBucketName());
        documento.setObjectKey(request.getObjectKey());
        documento.setCargadoPor(securityUtils.getCurrentUserId());

        documento = documentoRepository.save(documento);

        return DocumentoResponse.fromEntity(documento);
    }

    @Transactional
    public void eliminar(Long id) {
        GcDocumento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Documento no encontrado con ID: " + id));

        // Aqui se podria agregar logica para eliminar del Object Storage
        // ociStorageService.delete(documento.getBucketName(), documento.getObjectKey());

        documentoRepository.delete(documento);
    }
}
