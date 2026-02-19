package com.p11.masking.core;

public enum MaskStyle {

    FULL {
        @Override
        public String apply(String value, char maskChar) {
            if (value == null || value.isEmpty()) {
                return value;
            }
            return String.valueOf(maskChar).repeat(value.length());
        }
    },

    PARTIAL {
        @Override
        public String apply(String value, char maskChar) {
            if (value == null || value.isEmpty()) {
                return value;
            }
            if (value.length() <= 2) {
                return String.valueOf(maskChar).repeat(value.length());
            }
            int visibleChars = Math.max(1, value.length() / 4);
            String visible = value.substring(0, visibleChars);
            String masked = String.valueOf(maskChar).repeat(value.length() - visibleChars);
            return visible + masked;
        }
    },

    SHOW_LAST {
        @Override
        public String apply(String value, char maskChar) {
            if (value == null || value.isEmpty()) {
                return value;
            }
            int showCount = Math.min(4, value.length());
            String masked = String.valueOf(maskChar).repeat(value.length() - showCount);
            String visible = value.substring(value.length() - showCount);
            return masked + visible;
        }
    };

    public abstract String apply(String value, char maskChar);
}
