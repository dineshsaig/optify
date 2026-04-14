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

            // ── Greenhouse JSON feeds ────────────────────────────────────────────
            // To find a company's slug: look at their jobs URL, e.g.
            // https://boards.greenhouse.io/stripe → slug is "stripe"

            new CompanySource("Cloudflare",
                    "https://boards-api.greenhouse.io/v1/boards/cloudflare/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Figma",
                    "https://boards-api.greenhouse.io/v1/boards/figma/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Notion",
                    "https://boards-api.greenhouse.io/v1/boards/notion/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Stripe",
                    "https://boards-api.greenhouse.io/v1/boards/stripe/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Coinbase",
                    "https://boards-api.greenhouse.io/v1/boards/coinbase/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Databricks",
                    "https://boards-api.greenhouse.io/v1/boards/databricks/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Anthropic",
                    "https://boards-api.greenhouse.io/v1/boards/anthropic/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Reddit",
                    "https://boards-api.greenhouse.io/v1/boards/reddit/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Robinhood",
                    "https://boards-api.greenhouse.io/v1/boards/robinhood/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Pinterest",
                    "https://boards-api.greenhouse.io/v1/boards/pinterest/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("MongoDB",
                    "https://boards-api.greenhouse.io/v1/boards/mongodb/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("HubSpot",
                    "https://boards-api.greenhouse.io/v1/boards/hubspot/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Brex",
                    "https://boards-api.greenhouse.io/v1/boards/brex/jobs?content=true",
                    SourceType.JSON),
            new CompanySource("Plaid",
                    "https://boards-api.greenhouse.io/v1/boards/plaid/jobs?content=true",
                    SourceType.JSON),

            // ── Lever JSON feeds ─────────────────────────────────────────────────
            // To find a slug: look at their jobs URL, e.g.
            // https://jobs.lever.co/vercel → slug is "vercel"

            new CompanySource("Vercel",
                    "https://api.lever.co/v0/postings/vercel?mode=json",
                    SourceType.JSON),
            new CompanySource("Linear",
                    "https://api.lever.co/v0/postings/linear?mode=json",
                    SourceType.JSON),
            new CompanySource("Airtable",
                    "https://api.lever.co/v0/postings/airtable?mode=json",
                    SourceType.JSON),
            new CompanySource("Zapier",
                    "https://api.lever.co/v0/postings/zapier?mode=json",
                    SourceType.JSON)

            // ── HTML example (uncomment + tune CSS selector in JobParseService) ──
            // new CompanySource("ExampleCo",
            //         "https://www.example.com/careers",
            //         SourceType.HTML),
        );
    }
}
