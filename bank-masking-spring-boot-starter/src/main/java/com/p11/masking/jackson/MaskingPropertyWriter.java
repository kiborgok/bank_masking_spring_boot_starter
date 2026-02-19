package com.p11.masking.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.p11.masking.annotation.Mask;
import com.p11.masking.config.MaskingProperties;
import com.p11.masking.core.MaskStyle;

public class MaskingPropertyWriter extends BeanPropertyWriter {

    private final MaskingProperties properties;
    private final Mask maskAnnotation;

    protected MaskingPropertyWriter(BeanPropertyWriter base, MaskingProperties properties) {
        super(base);
        this.properties = properties;
        this.maskAnnotation = base.getAnnotation(Mask.class);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
            throws Exception {
        Object rawValue = get(bean);
        if (rawValue == null) {
            if (!_suppressNulls) {
                gen.writeFieldName(_name.getValue());
                gen.writeNull();
            }
            return;
        }
        gen.writeStringField(_name.getValue(), applyMask(rawValue.toString()));
    }

    @Override
    public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov)
            throws Exception {
        Object rawValue = get(bean);
        if (rawValue == null) {
            if (!_suppressNulls) {
                gen.writeNull();
            }
            return;
        }
        gen.writeString(applyMask(rawValue.toString()));
    }

    private String applyMask(String value) {
        if (value == null) return null;
        MaskStyle style;
        char maskChar;
        if (maskAnnotation != null) {
            style = maskAnnotation.style();
            maskChar = (maskAnnotation.maskChar() == '\0')
                    ? properties.getMaskCharacter()
                    : maskAnnotation.maskChar();
        } else {
            style = properties.getMaskStyle();
            maskChar = properties.getMaskCharacter();
        }
        return style.apply(value, maskChar);
    }
}
