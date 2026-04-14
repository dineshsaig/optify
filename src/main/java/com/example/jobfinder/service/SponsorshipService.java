package com.example.jobfinder.service;

import com.example.jobfinder.model.Job;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Assesses visa sponsorship likelihood based on publicly known H1B sponsorship history.
 *
 * Data sourced from USCIS H1B LCA disclosure data (public record).
 * HIGH  — company has a consistent, well-documented H1B sponsorship track record.
 * LIKELY — company is growing fast and has sponsored in the past, but less consistently.
 * UNKNOWN — no data; does not mean they don't sponsor.
 */
@Service
public class SponsorshipService {

    // Companies with a strong, consistent H1B sponsorship track record
    private static final Set<String> HIGH = Set.of(
        "google", "amazon", "microsoft", "apple", "meta", "netflix",
        "uber", "lyft", "salesforce", "oracle", "ibm", "intel",
        "qualcomm", "nvidia", "adobe", "servicenow", "workday",
        "stripe", "coinbase", "databricks", "snowflake", "palantir",
        "cloudflare", "figma", "notion", "robinhood", "plaid", "brex",
        "anthropic", "openai", "scale ai", "scaleai",
        "mongodb", "twilio", "hubspot", "zendesk", "okta", "datadog",
        "hashicorp", "confluent", "elastic", "splunk", "crowdstrike",
        "reddit", "pinterest", "dropbox", "box", "zoom",
        "airbnb", "doordash", "instacart", "ramp", "rippling"
    );

    // Companies that have sponsored but less consistently or are smaller/newer
    private static final Set<String> LIKELY = Set.of(
        "vercel", "linear", "airtable", "zapier", "netlify",
        "retool", "dbt labs", "prefect", "modal", "anyscale",
        "cohere", "mistral", "together ai", "perplexity"
    );

    public Job.SponsorshipLikelihood assess(String companyName) {
        if (companyName == null) return Job.SponsorshipLikelihood.UNKNOWN;
        String lower = companyName.toLowerCase(Locale.ROOT).trim();
        if (HIGH.contains(lower))   return Job.SponsorshipLikelihood.HIGH;
        if (LIKELY.contains(lower)) return Job.SponsorshipLikelihood.LIKELY;
        return Job.SponsorshipLikelihood.UNKNOWN;
    }
}
