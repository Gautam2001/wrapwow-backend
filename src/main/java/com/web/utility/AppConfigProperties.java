package com.web.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.config")
@Data
public class AppConfigProperties {
	private String baseURL;
	private String resourceLocation;
	private String imageUploadDIR;
}
