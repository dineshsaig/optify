package com.example.jobfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Value("${app.site.url:http://localhost:8080}")
    private String siteUrl;

    @ModelAttribute("siteUrl")
    public String siteUrl() {
        return siteUrl;
    }
}
