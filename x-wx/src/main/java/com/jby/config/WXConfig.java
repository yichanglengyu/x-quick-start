package com.jby.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class WXConfig {

    @Value("${wx-app-id: null}")
    private String appId;

    @Value("${wx-app-secret: null}")
    private String secret;
}
