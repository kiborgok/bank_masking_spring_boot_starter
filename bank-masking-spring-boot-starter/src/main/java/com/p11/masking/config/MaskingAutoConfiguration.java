package com.p11.masking.config;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.p11.masking.core.MaskingService;
import com.p11.masking.jackson.MaskingBeanSerializerModifier;
import com.p11.masking.logback.MaskingTurboFilter;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "p11.masking",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(MaskingProperties.class)
public class MaskingAutoConfiguration {

    @Bean(name = "maskingObjectMapper")
    @ConditionalOnMissingBean(name = "maskingObjectMapper")
    public ObjectMapper maskingObjectMapper(MaskingProperties properties) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        SimpleModule maskingModule = new SimpleModule("MaskingModule");
        maskingModule.setSerializerModifier(new MaskingBeanSerializerModifier(properties));
        mapper.registerModule(maskingModule);

        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public MaskingService maskingService(
            MaskingProperties properties,
            @Qualifier("maskingObjectMapper") ObjectMapper maskingObjectMapper) {
        return new MaskingService(properties, maskingObjectMapper);
    }

    @Bean
    public MaskingTurboFilter maskingTurboFilter(MaskingService maskingService) {
        MaskingTurboFilter filter = new MaskingTurboFilter(maskingService);
        filter.setName("MaskingTurboFilter");
        filter.start();

        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof LoggerContext loggerContext) {
            loggerContext.addTurboFilter(filter);
        }

        return filter;
    }
}
