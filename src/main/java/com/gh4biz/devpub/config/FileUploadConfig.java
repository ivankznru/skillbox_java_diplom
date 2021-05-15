package com.gh4biz.devpub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class FileUploadConfig {
    @Value("${blogUploadImageSizeLimit}")
    private String blogUploadImageSizeLimit;
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver
                = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Integer.parseInt(blogUploadImageSizeLimit));
        return multipartResolver;
    }
}
