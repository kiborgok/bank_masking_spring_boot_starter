package com.p11.masking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.p11.masking.config.MaskingProperties;
import com.p11.masking.core.MaskStyle;
import com.p11.masking.core.MaskingService;
import com.p11.masking.jackson.MaskingBeanSerializerModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaskingService")
class MaskingServiceTest {

    private MaskingProperties properties;
    private MaskingService maskingService;

    @BeforeEach
    void setUp() {
        properties = new MaskingProperties();
        properties.setEnabled(true);
        properties.setFields(List.of("email", "phoneNumber", "ssn"));
        properties.setMaskStyle(MaskStyle.PARTIAL);
        properties.setMaskCharacter('*');

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new MaskingBeanSerializerModifier(properties));
        mapper.registerModule(module);

        maskingService = new MaskingService(properties, mapper);
    }

    // mask(String) tests

    @Nested
    @DisplayName("mask(String)")
    class MaskString {

        @Test
        void masksValueWithGlobalStyle() {
            String result = maskingService.mask("john@example.com");
            assertThat(result).isNotEqualTo("john@example.com");
            assertThat(result).contains("*");
        }

        @Test
        void returnsNullForNullInput() {
            assertThat(maskingService.mask((String) null)).isNull();
        }

        @Test
        void returnValueWhenDisabled() {
            properties.setEnabled(false);
            assertThat(maskingService.mask("sensitive")).isEqualTo("sensitive");
        }
    }

    // toMaskedJson tests

    @Nested
    @DisplayName("toMaskedJson(Object)")
    class ToMaskedJson {

        @Test
        void masksConfiguredFieldsInSerialisation() throws Exception {
            TestDto dto = new TestDto("john@example.com", "+254712345678", "John Doe");
            String json = maskingService.toMaskedJson(dto);

            assertThat(json).doesNotContain("john@example.com");
            assertThat(json).doesNotContain("+254712345678");
            assertThat(json).contains("John Doe");  // non-sensitive field unchanged
        }

        @Test
        void returnsNullStringForNullInput() {
            assertThat(maskingService.toMaskedJson(null)).isEqualTo("null");
        }

        @Test
        void handlesNestedObjects() throws Exception {
            NestedDto dto = new NestedDto("parent", new TestDto("nested@test.com", "0700000000", "Jane"));
            String json = maskingService.toMaskedJson(dto);

            assertThat(json).doesNotContain("nested@test.com");
            assertThat(json).doesNotContain("0700000000");
            assertThat(json).contains("parent");
        }

        @Test
        void handlesNullFieldsGracefully() throws Exception {
            TestDto dto = new TestDto(null, null, "Author");
            String json = maskingService.toMaskedJson(dto);
            assertThat(json).contains("Author");
        }
    }

    // Helper DTOs

    static class TestDto {
        public final String email;
        public final String phoneNumber;
        public final String name;

        TestDto(String email, String phoneNumber, String name) {
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.name = name;
        }
    }

    static class NestedDto {
        public final String label;
        public final TestDto inner;

        NestedDto(String label, TestDto inner) {
            this.label = label;
            this.inner = inner;
        }
    }
}
