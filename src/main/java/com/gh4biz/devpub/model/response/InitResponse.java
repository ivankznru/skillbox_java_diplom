package com.gh4biz.devpub.model.response;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitResponse {
    @Value("${blogTitle}")
    private String title;
    @Value("${blogSubtitle}")
    private String subtitle;
    @Value("${blogPhone}")
    private String phone;
    @Value("${blogEmail}")
    private String email;
    @Value("${blogCopyright}")
    private String copyright;
    @Value("${blogCopyrightfrom}")
    private String copyrightFrom;
}
