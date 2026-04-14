package com.example.jobfinder.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"applyUrl"})
})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;

    @Column(length = 2048)
    private String applyUrl;

    private String source;
    private String postedDate;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;

    // NEW = spotted in the most recent refresh cycle
    private boolean isNew;

    // ACTIVE = still visible on the source page
    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.ACTIVE;

    // Likelihood this company sponsors H1B / work visas
    @Enumerated(EnumType.STRING)
    private SponsorshipLikelihood sponsorshipLikelihood = SponsorshipLikelihood.UNKNOWN;

    public enum JobStatus {
        ACTIVE, EXPIRED
    }

    public enum SponsorshipLikelihood {
        HIGH, LIKELY, UNKNOWN
    }

    // ---- constructors ----

    public Job() {}

    public Job(String title, String company, String location,
               String applyUrl, String source, String postedDate) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.applyUrl = applyUrl;
        this.source = source;
        this.postedDate = postedDate;
        this.firstSeenAt = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
        this.isNew = true;
        this.status = JobStatus.ACTIVE;
    }

    // ---- getters & setters ----

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getApplyUrl() { return applyUrl; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public LocalDateTime getFirstSeenAt() { return firstSeenAt; }
    public void setFirstSeenAt(LocalDateTime firstSeenAt) { this.firstSeenAt = firstSeenAt; }

    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public boolean isNew() { return isNew; }
    public void setNew(boolean aNew) { isNew = aNew; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public SponsorshipLikelihood getSponsorshipLikelihood() { return sponsorshipLikelihood; }
    public void setSponsorshipLikelihood(SponsorshipLikelihood sponsorshipLikelihood) {
        this.sponsorshipLikelihood = sponsorshipLikelihood;
    }
}
