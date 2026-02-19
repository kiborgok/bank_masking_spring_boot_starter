package com.p11.masking.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.p11.masking.annotation.Mask;
import com.p11.masking.config.MaskingProperties;

import java.util.List;

public class MaskingBeanSerializerModifier extends BeanSerializerModifier {

    private final MaskingProperties properties;

    public MaskingBeanSerializerModifier(MaskingProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                      BeanDescription beanDesc,
                                                      List<BeanPropertyWriter> beanProperties) {
        if (!properties.isEnabled()) {
            return beanProperties;
        }
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            if (isSensitive(writer)) {
                beanProperties.set(i, new MaskingPropertyWriter(writer, properties));
            }
        }
        return beanProperties;
    }

    private boolean isSensitive(BeanPropertyWriter writer) {
        return writer.getAnnotation(Mask.class) != null
                || properties.isSensitiveField(writer.getName());
    }
}
