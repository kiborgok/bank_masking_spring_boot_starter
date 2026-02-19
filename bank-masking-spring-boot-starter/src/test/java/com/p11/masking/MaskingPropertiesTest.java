package com.p11.masking;

import com.p11.masking.config.MaskingProperties;
import com.p11.masking.core.MaskStyle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingPropertiesTest {

    @Test
    void defaultsAreCorrect() {
        MaskingProperties props = new MaskingProperties();
        assertThat(props.isEnabled()).isTrue();
        assertThat(props.getMaskStyle()).isEqualTo(MaskStyle.PARTIAL);
        assertThat(props.getMaskCharacter()).isEqualTo('*');
        assertThat(props.getFields()).isEmpty();
    }

    @Test
    void isSensitiveFieldIsCaseInsensitive() {
        MaskingProperties props = new MaskingProperties();
        props.setFields(List.of("email", "phoneNumber"));

        assertThat(props.isSensitiveField("email")).isTrue();
        assertThat(props.isSensitiveField("EMAIL")).isTrue();
        assertThat(props.isSensitiveField("Email")).isTrue();
        assertThat(props.isSensitiveField("PHONENUMBER")).isTrue();
        assertThat(props.isSensitiveField("name")).isFalse();
        assertThat(props.isSensitiveField(null)).isFalse();
    }

    @Test
    void setFieldsPopulatesInternalSet() {
        MaskingProperties props = new MaskingProperties();
        props.setFields(List.of("ssn", "creditCardNumber"));
        assertThat(props.getFields()).containsExactlyInAnyOrder("ssn", "creditCardNumber");
    }
}
