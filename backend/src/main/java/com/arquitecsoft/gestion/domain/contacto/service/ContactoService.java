package com.arquitecsoft.gestion.domain.contacto.service;

import com.arquitecsoft.gestion.domain.contacto.dto.*;
import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto;
import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto.Categoria;
import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto.TipoCanal;
import com.arquitecsoft.gestion.domain.contacto.entity.GcRedSocial;
import com.arquitecsoft.gestion.domain.contacto.repository.GcContactoRepository;
import com.arquitecsoft.gestion.domain.contacto.repository.GcRedSocialRepository;
import com.arquitecsoft.gestion.domain.contacto.util.PhoneNumberNormalizer;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.persona.repository.GcPersonaRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ContactoService {

    private static final Pattern EMAIL_RE =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final GcContactoRepository contactoRepository;
    private final GcRedSocialRepository redSocialRepository;
    private final GcEmpresaRepository empresaRepository;
    private final GcPersonaRepository personaRepository;
    private final PhoneNumberNormalizer phoneNormalizer;
    private final SecurityUtils securityUtils;

    public ContactoService(GcContactoRepository contactoRepository,
                           GcRedSocialRepository redSocialRepository,
                           GcEmpresaRepository empresaRepository,
                           GcPersonaRepository personaRepository,
                           PhoneNumberNormalizer phoneNormalizer,
                           SecurityUtils securityUtils) {
        this.contactoRepository = contactoRepository;
        this.redSocialRepository = redSocialRepository;
        this.empresaRepository = empresaRepository;
        this.personaRepository = personaRepository;
        this.phoneNormalizer = phoneNormalizer;
        this.securityUtils = securityUtils;
    }

    // ====================== Catálogo ======================

    @Transactional(readOnly = true)
    public List<RedSocialResponse> listarRedesSociales() {
        return redSocialRepository.findByActivoOrderByOrdenAscNombreAsc(1).stream()
                .map(RedSocialResponse::fromEntity)
                .toList();
    }

    // ====================== Listados ======================

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarPorEmpresa(Long empresaId, String tipoCanal, boolean incluirInactivos) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new BusinessException("EMPRESA_NOT_FOUND", "Empresa no encontrada: " + empresaId);
        }
        TipoCanal filtro = parseTipoCanalOpcional(tipoCanal);
        return contactoRepository.findByEmpresa(empresaId, filtro, incluirInactivos).stream()
                .map(ContactoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarPorPersona(Long personaId, String tipoCanal, boolean incluirInactivos) {
        if (!personaRepository.existsById(personaId)) {
            throw new BusinessException("PERSONA_NOT_FOUND", "Persona no encontrada: " + personaId);
        }
        TipoCanal filtro = parseTipoCanalOpcional(tipoCanal);
        return contactoRepository.findByPersona(personaId, filtro, incluirInactivos).stream()
                .map(ContactoResponse::fromEntity)
                .toList();
    }

    // ====================== Alta ======================

    @Transactional
    public ContactoResponse crearParaEmpresa(Long empresaId, ContactoCreateRequest req) {
        var empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BusinessException("EMPRESA_NOT_FOUND", "Empresa no encontrada: " + empresaId));

        GcContacto c = new GcContacto();
        c.setEmpresa(empresa);
        // En empresa la categoría no aplica (RB-26): se deja nula.
        aplicarCamposComunes(c, req.getTipoCanal(), null, req.getEtiqueta(), req.getValor(),
                req.getIndicativoPais(), req.getExtension(), req.getEsWhatsapp(), req.getEnviable(),
                req.getRedSocialCodigo(), req.getUrl(), req.getEsPrincipal(), req.getObservaciones());

        validarDuplicadoEmpresa(empresaId, c, null);
        gestionarPrincipalEmpresa(empresaId, c, null);

        c.setCreadoPor(securityUtils.getCurrentUserId());
        c = contactoRepository.save(c);
        return ContactoResponse.fromEntity(recargar(c.getId()));
    }

    @Transactional
    public ContactoResponse crearParaPersona(Long personaId, ContactoCreateRequest req) {
        var persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new BusinessException("PERSONA_NOT_FOUND", "Persona no encontrada: " + personaId));

        // RB-26: categoría obligatoria en persona
        if (req.getCategoria() == null || req.getCategoria().isBlank()) {
            throw new BusinessException("RB_26_CATEGORIA_REQUERIDA",
                    "La categoría (PERSONAL / EMPRESARIAL / OTRO) es obligatoria para contactos de persona");
        }

        GcContacto c = new GcContacto();
        c.setPersona(persona);
        aplicarCamposComunes(c, req.getTipoCanal(), req.getCategoria(), req.getEtiqueta(), req.getValor(),
                req.getIndicativoPais(), req.getExtension(), req.getEsWhatsapp(), req.getEnviable(),
                req.getRedSocialCodigo(), req.getUrl(), req.getEsPrincipal(), req.getObservaciones());

        validarDuplicadoPersona(personaId, c, null);
        gestionarPrincipalPersona(personaId, c, null);

        c.setCreadoPor(securityUtils.getCurrentUserId());
        c = contactoRepository.save(c);
        return ContactoResponse.fromEntity(recargar(c.getId()));
    }

    // ====================== Actualización ======================

    @Transactional
    public ContactoResponse actualizar(Long id, ContactoUpdateRequest req) {
        GcContacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CONTACTO_NOT_FOUND", "Contacto no encontrado: " + id));

        // El tipoCanal no cambia en update; la categoría sí (solo aplica si es de persona)
        String categoria = c.getPersona() != null ? req.getCategoria() : null;
        if (c.getPersona() != null && (categoria == null || categoria.isBlank())) {
            throw new BusinessException("RB_26_CATEGORIA_REQUERIDA",
                    "La categoría es obligatoria para contactos de persona");
        }

        aplicarCamposComunes(c, c.getTipoCanal().name(), categoria, req.getEtiqueta(), req.getValor(),
                req.getIndicativoPais(), req.getExtension(), req.getEsWhatsapp(), req.getEnviable(),
                req.getRedSocialCodigo(), req.getUrl(), req.getEsPrincipal(), req.getObservaciones());

        if (c.getEmpresa() != null) {
            validarDuplicadoEmpresa(c.getEmpresa().getId(), c, id);
            gestionarPrincipalEmpresa(c.getEmpresa().getId(), c, id);
        } else {
            validarDuplicadoPersona(c.getPersona().getId(), c, id);
            gestionarPrincipalPersona(c.getPersona().getId(), c, id);
        }

        c.setModificadoPor(securityUtils.getCurrentUserId());
        contactoRepository.save(c);
        return ContactoResponse.fromEntity(recargar(id));
    }

    // ====================== Soft delete (RB-27) ======================

    @Transactional
    public void eliminar(Long id) {
        GcContacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CONTACTO_NOT_FOUND", "Contacto no encontrado: " + id));
        c.setActivo(0);
        c.setEsPrincipal(0); // un inactivo no puede ser principal
        c.setModificadoPor(securityUtils.getCurrentUserId());
        contactoRepository.save(c);
    }

    // ====================== Marcar principal (RB-25) ======================

    @Transactional
    public ContactoResponse marcarPrincipal(Long id) {
        GcContacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CONTACTO_NOT_FOUND", "Contacto no encontrado: " + id));
        if (!c.isActivo()) {
            throw new BusinessException("RB_31_MEDIO_NO_ACCIONABLE",
                    "No se puede marcar como principal un contacto inactivo");
        }
        c.setEsPrincipal(1);
        if (c.getEmpresa() != null) {
            gestionarPrincipalEmpresa(c.getEmpresa().getId(), c, id);
        } else {
            gestionarPrincipalPersona(c.getPersona().getId(), c, id);
        }
        c.setModificadoPor(securityUtils.getCurrentUserId());
        contactoRepository.save(c);
        return ContactoResponse.fromEntity(recargar(id));
    }

    // ====================== Acciones externas ======================

    @Transactional(readOnly = true)
    public AccionLlamadaResponse payloadLlamada(Long id) {
        GcContacto c = obtenerAccionable(id);
        if (c.getTipoCanal() != TipoCanal.TELEFONO && c.getTipoCanal() != TipoCanal.CELULAR) {
            throw new BusinessException("RB_31_MEDIO_NO_ACCIONABLE", "El contacto no es un número telefónico");
        }
        return AccionLlamadaResponse.fromEntity(c);
    }

    @Transactional(readOnly = true)
    public AccionCorreoResponse payloadCorreo(Long id) {
        GcContacto c = obtenerAccionable(id);
        if (c.getTipoCanal() != TipoCanal.EMAIL) {
            throw new BusinessException("RB_31_MEDIO_NO_ACCIONABLE", "El contacto no es un correo electrónico");
        }
        if (!c.isEnviable()) {
            throw new BusinessException("RB_31_MEDIO_NO_ACCIONABLE", "El correo está marcado como no enviable");
        }
        return AccionCorreoResponse.fromEntity(c);
    }

    // ====================== Helpers ======================

    private GcContacto obtenerAccionable(Long id) {
        GcContacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CONTACTO_NOT_FOUND", "Contacto no encontrado: " + id));
        if (!c.isActivo()) {
            throw new BusinessException("RB_31_MEDIO_NO_ACCIONABLE", "El contacto está inactivo");
        }
        return c;
    }

    private GcContacto recargar(Long id) {
        return contactoRepository.findByIdWithRelations(id).orElseThrow();
    }

    /**
     * Mapea y valida los campos comunes según el tipo de canal (RB-21..RB-24, RB-30).
     */
    private void aplicarCamposComunes(GcContacto c, String tipoCanalStr, String categoriaStr,
                                      String etiqueta, String valor, String indicativoPais, String extension,
                                      Boolean esWhatsapp, Boolean enviable, String redSocialCodigo, String url,
                                      Boolean esPrincipal, String observaciones) {

        TipoCanal tipoCanal = parseTipoCanal(tipoCanalStr);
        c.setTipoCanal(tipoCanal);
        c.setCategoria(parseCategoriaOpcional(categoriaStr));
        c.setEtiqueta(trimToNull(etiqueta));
        c.setObservaciones(trimToNull(observaciones));
        c.setEsPrincipal(Boolean.TRUE.equals(esPrincipal) ? 1 : 0);

        String valorLimpio = trimToNull(valor);
        if (valorLimpio == null) {
            throw new BusinessException("VALIDACION_VALOR", "El valor del contacto es requerido");
        }

        // Reset de campos específicos de otros canales
        c.setIndicativoPais(null);
        c.setNumeroE164(null);
        c.setExtension(null);
        c.setEsWhatsapp(0);
        c.setEnviable(1);
        c.setRedSocial(null);
        c.setUrl(null);

        switch (tipoCanal) {
            case TELEFONO, CELULAR -> {
                String ind = trimToNull(indicativoPais);
                if (ind == null) {
                    throw new BusinessException("RB_21_E164_REQUERIDO",
                            "El indicativo de país es obligatorio para teléfonos y celulares");
                }
                var r = phoneNormalizer.normalizar(ind, valorLimpio)
                        .orElseThrow(() -> new BusinessException("RB_21_E164_REQUERIDO",
                                "El número no es válido para el indicativo indicado; no se pudo normalizar a E.164"));
                c.setValor(valorLimpio);
                c.setIndicativoPais(r.getIndicativo());
                c.setNumeroE164(r.getE164());
                c.setExtension(trimToNull(extension));
                c.setEsWhatsapp(Boolean.TRUE.equals(esWhatsapp) ? 1 : 0); // RB-22
            }
            case EMAIL -> {
                if (Boolean.TRUE.equals(esWhatsapp)) {
                    throw new BusinessException("RB_22_WHATSAPP_INVALIDO",
                            "WhatsApp solo aplica a teléfonos o celulares");
                }
                if (!EMAIL_RE.matcher(valorLimpio).matches()) {
                    throw new BusinessException("RB_23_EMAIL_INVALIDO", "El correo no tiene un formato válido");
                }
                c.setValor(valorLimpio.toLowerCase());
                c.setEnviable(enviable == null || Boolean.TRUE.equals(enviable) ? 1 : 0);
            }
            case RED_SOCIAL -> {
                String cod = trimToNull(redSocialCodigo);
                String link = trimToNull(url);
                if (cod == null) {
                    throw new BusinessException("RB_24_RED_INVALIDA", "Debe indicarse la red social del catálogo");
                }
                GcRedSocial red = redSocialRepository.findById(cod)
                        .orElseThrow(() -> new BusinessException("RB_24_RED_INVALIDA",
                                "Red social no encontrada en el catálogo: " + cod));
                if (link == null || !(link.startsWith("http://") || link.startsWith("https://"))) {
                    throw new BusinessException("RB_24_RED_INVALIDA",
                            "La URL de la red social debe ser un enlace http(s) accesible");
                }
                c.setValor(valorLimpio);
                c.setRedSocial(red);
                c.setUrl(link);
            }
        }
    }

    // RB-28: no duplicar el mismo medio activo para el propietario
    private void validarDuplicadoEmpresa(Long empresaId, GcContacto candidato, Long idActual) {
        List<GcContacto> existentes =
                contactoRepository.findByEmpresa_IdAndTipoCanalAndActivo(empresaId, candidato.getTipoCanal(), 1);
        verificarDuplicado(existentes, candidato, idActual);
    }

    private void validarDuplicadoPersona(Long personaId, GcContacto candidato, Long idActual) {
        List<GcContacto> existentes =
                contactoRepository.findByPersona_IdAndTipoCanalAndActivo(personaId, candidato.getTipoCanal(), 1);
        verificarDuplicado(existentes, candidato, idActual);
    }

    private void verificarDuplicado(List<GcContacto> existentes, GcContacto candidato, Long idActual) {
        String claveNueva = claveComparacion(candidato);
        for (GcContacto e : existentes) {
            if (idActual != null && e.getId().equals(idActual)) continue;
            if (claveComparacion(e).equalsIgnoreCase(claveNueva)) {
                throw new BusinessException("RB_28_CONTACTO_DUPLICADO",
                        "Ya existe un contacto activo igual para este propietario");
            }
        }
    }

    private String claveComparacion(GcContacto c) {
        return c.getNumeroE164() != null ? c.getNumeroE164() : c.getValor();
    }

    // RB-25: a lo sumo un principal por (propietario, tipoCanal); marcar uno desmarca los otros
    private void gestionarPrincipalEmpresa(Long empresaId, GcContacto candidato, Long idActual) {
        if (!candidato.isPrincipal()) return;
        for (GcContacto e : contactoRepository.findByEmpresa_IdAndTipoCanalAndActivo(empresaId, candidato.getTipoCanal(), 1)) {
            if (idActual != null && e.getId().equals(idActual)) continue;
            if (e.isPrincipal()) {
                e.setEsPrincipal(0);
                contactoRepository.save(e);
            }
        }
    }

    private void gestionarPrincipalPersona(Long personaId, GcContacto candidato, Long idActual) {
        if (!candidato.isPrincipal()) return;
        for (GcContacto e : contactoRepository.findByPersona_IdAndTipoCanalAndActivo(personaId, candidato.getTipoCanal(), 1)) {
            if (idActual != null && e.getId().equals(idActual)) continue;
            if (e.isPrincipal()) {
                e.setEsPrincipal(0);
                contactoRepository.save(e);
            }
        }
    }

    private TipoCanal parseTipoCanal(String s) {
        try {
            return TipoCanal.valueOf(s);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BusinessException("VALIDACION_TIPO_CANAL",
                    "Tipo de canal inválido. Use TELEFONO, CELULAR, EMAIL o RED_SOCIAL");
        }
    }

    private TipoCanal parseTipoCanalOpcional(String s) {
        if (s == null || s.isBlank()) return null;
        return parseTipoCanal(s);
    }

    private Categoria parseCategoriaOpcional(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Categoria.valueOf(s);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("VALIDACION_CATEGORIA",
                    "Categoría inválida. Use PERSONAL, EMPRESARIAL u OTRO");
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
