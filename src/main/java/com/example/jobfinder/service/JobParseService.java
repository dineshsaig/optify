package com.example.jobfinder.service;

import com.example.jobfinder.model.CompanySource;
import com.example.jobfinder.model.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses raw content (JSON or HTML) into a list of Job objects.
 *
 * Supported JSON formats:
 *   - Greenhouse  — { "jobs": [ { "title", "location.name", "absolute_url", "updated_at" } ] }
 *   - Lever       — [ { "text", "categories.location", "hostedUrl", "createdAt" } ]
 *
 * HTML:
 *   - Generic Jsoup scrape; adjust the CSS selectors for each site as needed.
 */
@Service
public class JobParseService {

    private static final Logger log = LoggerFactory.getLogger(JobParseService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Job> parse(String content, CompanySource source) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        return switch (source.getType()) {
            case JSON -> parseJson(content, source);
            case HTML -> parseHtml(content, source);
        };
    }

    // -------------------------------------------------------------------------
    // JSON parsing
    // -------------------------------------------------------------------------

    private List<Job> parseJson(String content, CompanySource source) {
        try {
            JsonNode root = mapper.readTree(content);

            // Greenhouse: root is an object with a "jobs" array
            if (root.isObject() && root.has("jobs")) {
                return parseGreenhouse(root.get("jobs"), source);
            }

            // Lever: root is an array of postings
            if (root.isArray()) {
                return parseLever(root, source);
            }

            log.warn("Unknown JSON format for {}", source.getCompany());
            return List.of();

        } catch (Exception e) {
            log.error("JSON parse error for {}: {}", source.getCompany(), e.getMessage());
            return List.of();
        }
    }

    private List<Job> parseGreenhouse(JsonNode jobsArray, CompanySource source) {
        List<Job> jobs = new ArrayList<>();
        for (JsonNode node : jobsArray) {
            String title    = text(node, "title");
            String location = node.path("location").path("name").asText("Remote");
            String url      = text(node, "absolute_url");
            String posted   = text(node, "updated_at");

            if (!title.isBlank() && !url.isBlank()) {
                jobs.add(new Job(title, source.getCompany(), location, url, source.getCompany(), posted));
            }
        }
        return jobs;
    }

    private List<Job> parseLever(JsonNode postingsArray, CompanySource source) {
        List<Job> jobs = new ArrayList<>();
        for (JsonNode node : postingsArray) {
            String title    = text(node, "text");
            String location = node.path("categories").path("location").asText("Remote");
            String url      = text(node, "hostedUrl");

            // Lever stores createdAt as epoch millis
            String posted = "";
            JsonNode createdAt = node.path("createdAt");
            if (!createdAt.isMissingNode()) {
                posted = String.valueOf(createdAt.asLong());
            }

            if (!title.isBlank() && !url.isBlank()) {
                jobs.add(new Job(title, source.getCompany(), location, url, source.getCompany(), posted));
            }
        }
        return jobs;
    }

    // -------------------------------------------------------------------------
    // HTML parsing (Jsoup)
    // -------------------------------------------------------------------------

    private List<Job> parseHtml(String content, CompanySource source) {
        List<Job> jobs = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(content, source.getUrl());

            /*
             * Generic heuristic: look for <a> tags whose text looks like a job title
             * and whose href contains common career path segments.
             *
             * This works as a starting point for simple career pages.
             * For a specific company, replace this block with targeted CSS selectors,
             * e.g.: doc.select("li.job-listing a")
             */
            for (Element link : doc.select("a[href]")) {
                String href = link.absUrl("href");
                String title = link.text().trim();

                if (title.isBlank() || title.length() < 5) continue;
                if (!href.contains("/job") && !href.contains("/career")
                        && !href.contains("/position") && !href.contains("/opening")) {
                    continue;
                }

                jobs.add(new Job(title, source.getCompany(), "See listing", href, source.getCompany(), ""));
            }
        } catch (Exception e) {
            log.error("HTML parse error for {}: {}", source.getCompany(), e.getMessage());
        }
        return jobs;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String text(JsonNode node, String field) {
        JsonNode n = node.path(field);
        return n.isMissingNode() ? "" : n.asText("").trim();
    }
}
