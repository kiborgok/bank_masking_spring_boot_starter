package com.p11.masking.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p11.masking.config.MaskingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaskingService {

    private static final Logger log = LoggerFactory.getLogger(MaskingService.class);

    private final MaskingProperties properties;
    private final ObjectMapper maskingObjectMapper;

    public MaskingService(MaskingProperties properties, ObjectMapper maskingObjectMapper) {
        this.properties = properties;
        this.maskingObjectMapper = maskingObjectMapper;
    }

    public String toMaskedJson(Object object) {
        if (object == null) {
            return "null";
        }
        try {
            return maskingObjectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.warn("MaskingService: failed to serialise object of type {}: {}",
                    object.getClass().getSimpleName(), e.getMessage());
            return object.toString();
        }
    }

    public String mask(String value) {
        if (!properties.isEnabled() || value == null) {
            return value;
        }
        return properties.getMaskStyle().apply(value, properties.getMaskCharacter());
    }


    public String mask(String value, MaskStyle style, char maskChar) {
        if (!properties.isEnabled() || value == null) {
            return value;
        }
        char effectiveMaskChar = (maskChar == '\0') ? properties.getMaskCharacter() : maskChar;
        return style.apply(value, effectiveMaskChar);
    }

    public MaskingProperties getProperties() {
        return properties;
    }
}
