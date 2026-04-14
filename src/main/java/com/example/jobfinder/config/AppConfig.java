package com.example.jobfinder.config;

import com.example.jobfinder.model.CompanySource;
import com.example.jobfinder.model.CompanySource.SourceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
public class AppConfig {

    /**
     * Shared HttpClient used by the fetcher service.
     * Follows redirects and has a 15-second timeout.
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * The list of company career pages to monitor.
     *
     * To add a new source:
     *   1. Find the company's public careers/jobs page URL.
     *   2. If it returns JSON (e.g. Greenhouse, Lever, Ashby boards), use SourceType.JSON.
     *   3. If it is a plain HTML page, use SourceType.HTML (Jsoup will scrape it).
     *
     * Examples below show real public ATS JSON feeds which are freely accessible.
     */
    @Bean
    public List<CompanySource> companySources() {
        return List.of(

            // --- Greenhouse JSON feeds (append ?content=true for full job data) ---
            new CompanySource("Cloudflare",
                    "https://boards-api.greenhouse.io/v1/boards/cloudflare/jobs?content=true",
                    SourceType.JSON),

            new CompanySource("Figma",
                    "https://boards-api.greenhouse.io/v1/boards/figma/jobs?content=true",
                    SourceType.JSON),

            new CompanySource("Notion",
                    "https://boards-api.greenhouse.io/v1/boards/notion/jobs?content=true",
                    SourceType.JSON),

            // --- Lever JSON feeds ---
            new CompanySource("Vercel",
                    "https://api.lever.co/v0/postings/vercel?mode=json",
                    SourceType.JSON),

            new CompanySource("Linear",
                    "https://api.lever.co/v0/postings/linear?mode=json",
                    SourceType.JSON)

            // --- HTML example (uncomment and adjust CSS selector in JobParseService) ---
            // new CompanySource("ExampleCo",
            //         "https://www.example.com/careers",
            //         SourceType.HTML),
        );
    }
}
