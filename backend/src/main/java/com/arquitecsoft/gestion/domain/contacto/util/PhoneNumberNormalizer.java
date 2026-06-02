package com.arquitecsoft.gestion.domain.contacto.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Normaliza números telefónicos a formato E.164 usando libphonenumber.
 * Reemplaza la normalización ingenua de F-CT1 (RB-21).
 *
 * Estrategia tolerante (apropiada para un CRM): acepta el número si libphonenumber
 * logra parsearlo y lo considera "posible" para la región del indicativo, aunque no
 * sea estrictamente "válido". Devuelve {@code Optional.empty()} cuando no es ni posible.
 */
@Component
public class PhoneNumberNormalizer {

    private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();

    /**
     * @param indicativoPais indicativo del país, con o sin '+' (ej. "+57", "57").
     * @param numeroCrudo    número tal como lo digita el usuario (puede traer espacios,
     *                       guiones, paréntesis e incluso el propio indicativo).
     * @return el número normalizado, o {@code Optional.empty()} si no es procesable.
     */
    public Optional<ResultadoNormalizacion> normalizar(String indicativoPais, String numeroCrudo) {
        if (numeroCrudo == null || numeroCrudo.isBlank()) {
            return Optional.empty();
        }

        Integer callingCode = extraerCallingCode(indicativoPais);
        if (callingCode == null) {
            return Optional.empty();
        }

        String region = util.getRegionCodeForCountryCode(callingCode);

        try {
            // Si el número ya viene en formato internacional (con '+'), se parsea directo;
            // si no, se le antepone el indicativo para que libphonenumber lo interprete.
            String entrada = numeroCrudo.trim().startsWith("+")
                    ? numeroCrudo.trim()
                    : "+" + callingCode + " " + numeroCrudo.trim();

            PhoneNumber parsed = util.parse(entrada, region);

            if (!util.isPossibleNumber(parsed)) {
                return Optional.empty();
            }

            String e164 = util.format(parsed, PhoneNumberFormat.E164);
            String nacional = util.format(parsed, PhoneNumberFormat.NATIONAL);
            String indicativoNorm = "+" + parsed.getCountryCode();
            boolean valido = util.isValidNumber(parsed);

            return Optional.of(new ResultadoNormalizacion(e164, indicativoNorm, nacional, valido));
        } catch (NumberParseException e) {
            return Optional.empty();
        }
    }

    private Integer extraerCallingCode(String indicativoPais) {
        if (indicativoPais == null) return null;
        String soloDigitos = indicativoPais.replaceAll("[^0-9]", "");
        if (soloDigitos.isEmpty()) return null;
        try {
            return Integer.parseInt(soloDigitos);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Resultado de la normalización. Inmutable.
     */
    public static class ResultadoNormalizacion {
        private final String e164;
        private final String indicativo;       // "+57"
        private final String formatoNacional;  // visualización local
        private final boolean valido;           // true si pasa la validación estricta

        public ResultadoNormalizacion(String e164, String indicativo, String formatoNacional, boolean valido) {
            this.e164 = e164;
            this.indicativo = indicativo;
            this.formatoNacional = formatoNacional;
            this.valido = valido;
        }

        public String getE164() { return e164; }
        public String getIndicativo() { return indicativo; }
        public String getFormatoNacional() { return formatoNacional; }
        public boolean isValido() { return valido; }
    }
}
