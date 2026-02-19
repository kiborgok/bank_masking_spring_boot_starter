package com.p11.masking.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.p11.masking.core.MaskingService;
import org.slf4j.Marker;

public class MaskingTurboFilter extends TurboFilter {

    private final MaskingService maskingService;

    public MaskingTurboFilter(MaskingService maskingService) {
        this.maskingService = maskingService;
    }

    @Override
    public FilterReply decide(Marker marker,
                               Logger logger,
                               Level level,
                               String format,
                               Object[] params,
                               Throwable t) {

        if (!maskingService.getProperties().isEnabled() || params == null || params.length == 0) {
            return FilterReply.NEUTRAL;
        }

        if (!logger.isEnabledFor(level)) {
            return FilterReply.NEUTRAL;
        }

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param != null && isMaskable(param)) {
                params[i] = maskingService.toMaskedJson(param);
            }
        }

        return FilterReply.NEUTRAL;
    }

    private boolean isMaskable(Object param) {
        if (param instanceof String
                || param instanceof Number
                || param instanceof Boolean
                || param instanceof Character) {
            return false;
        }

        if (param instanceof Throwable) {
            return false;
        }
        return true;
    }
}
