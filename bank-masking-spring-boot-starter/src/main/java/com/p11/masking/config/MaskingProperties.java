package com.p11.masking.config;

import com.p11.masking.core.MaskStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "p11.masking")
public class MaskingProperties {

    private boolean enabled = true;

    private Set<String> fields = new HashSet<>();

    private MaskStyle maskStyle = MaskStyle.PARTIAL;

    private char maskCharacter = '*';

    // Getters & Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        if (fields != null) {
            this.fields = new HashSet<>(fields);
        }
    }

    public MaskStyle getMaskStyle() {
        return maskStyle;
    }

    public void setMaskStyle(MaskStyle maskStyle) {
        this.maskStyle = maskStyle;
    }

    public char getMaskCharacter() {
        return maskCharacter;
    }

    public void setMaskCharacter(char maskCharacter) {
        this.maskCharacter = maskCharacter;
    }

    /**
     * Convenience method – returns true if the given field name is configured
     * as sensitive (case-insensitive comparison).
     */
    public boolean isSensitiveField(String fieldName) {
        if (fieldName == null) {
            return false;
        }
        return fields.stream().anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }
}
