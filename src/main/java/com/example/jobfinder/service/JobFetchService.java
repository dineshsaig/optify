package com.example.jobfinder.service;

import com.example.jobfinder.model.CompanySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Fetches raw content (HTML or JSON string) from a company career page.
 */
@Service
public class JobFetchService {

    private static final Logger log = LoggerFactory.getLogger(JobFetchService.class);

    private final HttpClient httpClient;

    public JobFetchService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Fetches the body of the given URL as a plain string.
     * Returns null if the request fails or returns a non-200 status.
     */
    public String fetch(CompanySource source) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(source.getUrl()))
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent",
                            "Mozilla/5.0 (compatible; JobFinderBot/1.0; +https://github.com/example/job-finder)")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                log.warn("Non-200 status {} fetching {}: {}",
                        response.statusCode(), source.getCompany(), source.getUrl());
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to fetch jobs from {}: {}", source.getCompany(), e.getMessage());
            return null;
        }
    }
}
