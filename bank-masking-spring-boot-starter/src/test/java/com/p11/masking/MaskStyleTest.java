package com.p11.masking;

import com.p11.masking.core.MaskStyle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaskStyle")
class MaskStyleTest {

    @Nested
    @DisplayName("FULL style")
    class FullStyle {

        @Test
        void replacesAllCharsWithMaskChar() {
            assertThat(MaskStyle.FULL.apply("secret123", '*')).isEqualTo("*********");
        }

        @Test
        void singleCharIsFullyMasked() {
            assertThat(MaskStyle.FULL.apply("x", '#')).isEqualTo("#");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void returnsNullOrEmptyUnchanged(String input) {
            assertThat(MaskStyle.FULL.apply(input, '*')).isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("PARTIAL style")
    class PartialStyle {

        @Test
        void showsFirstPortionAndMasksRest() {
            String result = MaskStyle.PARTIAL.apply("email@example.com", '*');
            assertThat(result).startsWith("e");
            assertThat(result).contains("*");
            assertThat(result).hasSize("email@example.com".length());
        }

        @Test
        void veryShortValueIsFullyMasked() {
            assertThat(MaskStyle.PARTIAL.apply("ab", '*')).isEqualTo("**");
            assertThat(MaskStyle.PARTIAL.apply("a", '*')).isEqualTo("*");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void returnsNullOrEmptyUnchanged(String input) {
            assertThat(MaskStyle.PARTIAL.apply(input, '*')).isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("SHOW_LAST style")
    class ShowLastStyle {

        @Test
        void masksAllButLastFourChars() {
            String result = MaskStyle.SHOW_LAST.apply("4111111111111111", '*');
            assertThat(result).endsWith("1111");
            assertThat(result).startsWith("************");
            assertThat(result).hasSize(16);
        }

        @Test
        void shortValueShowsAll() {
            assertThat(MaskStyle.SHOW_LAST.apply("abc", '*')).isEqualTo("abc");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void returnsNullOrEmptyUnchanged(String input) {
            assertThat(MaskStyle.SHOW_LAST.apply(input, '*')).isEqualTo(input);
        }
    }
}
