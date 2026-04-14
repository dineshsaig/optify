package com.example.jobfinder.model;

/**
 * Represents one career page target.
 * type: JSON  — the page returns a JSON feed we can parse
 * type: HTML  — we use Jsoup to scrape the HTML
 */
public class CompanySource {

    private final String company;
    private final String url;
    private final SourceType type;

    public enum SourceType { JSON, HTML }

    public CompanySource(String company, String url, SourceType type) {
        this.company = company;
        this.url = url;
        this.type = type;
    }

    public String getCompany() { return company; }
    public String getUrl()     { return url; }
    public SourceType getType() { return type; }
}
