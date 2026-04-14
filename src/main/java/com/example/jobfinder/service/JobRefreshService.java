package com.example.jobfinder.service;

import com.example.jobfinder.model.CompanySource;
import com.example.jobfinder.model.Job;
import com.example.jobfinder.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates one full refresh cycle:
 *   1. Clear isNew flags from the previous cycle.
 *   2. For each source: fetch → parse → score → save new jobs.
 */
@Service
public class JobRefreshService {

    private static final Logger log = LoggerFactory.getLogger(JobRefreshService.class);

    private final List<CompanySource>  sources;
    private final JobFetchService      fetcher;
    private final JobParseService      parser;
    private final JobScoringService    scorer;
    private final JobRepository        repository;

    public JobRefreshService(List<CompanySource> sources,
                             JobFetchService fetcher,
                             JobParseService parser,
                             JobScoringService scorer,
                             JobRepository repository) {
        this.sources    = sources;
        this.fetcher    = fetcher;
        this.parser     = parser;
        this.scorer     = scorer;
        this.repository = repository;
    }

    @Transactional
    public RefreshResult refresh() {
        log.info("Starting refresh cycle at {}", LocalDateTime.now());

        // Step 1 — clear previous "new" markers
        repository.clearAllNewFlags();

        int newCount   = 0;
        int totalSeen  = 0;

        // Step 2 — process each source
        for (CompanySource source : sources) {
            log.info("Fetching from {}", source.getCompany());

            String content = fetcher.fetch(source);
            if (content == null) continue;

            List<Job> parsed = parser.parse(content, source);
            totalSeen += parsed.size();

            for (Job job : parsed) {
                // Only keep relevant jobs (keyword filter)
                if (!scorer.isRelevant(job)) continue;

                Optional<Job> existing = repository.findByApplyUrl(job.getApplyUrl());
                if (existing.isEmpty()) {
                    // Brand-new job — save it
                    repository.save(job);
                    newCount++;
                    log.debug("New job: {} @ {}", job.getTitle(), job.getCompany());
                } else {
                    // Already known — mark as still active and bump lastSeenAt
                    Job known = existing.get();
                    known.setLastSeenAt(LocalDateTime.now());
                    known.setStatus(Job.JobStatus.ACTIVE);
                    repository.save(known);
                }
            }
        }

        log.info("Refresh complete — {} new jobs out of {} seen", newCount, totalSeen);
        return new RefreshResult(newCount, totalSeen, LocalDateTime.now());
    }

    // -------------------------------------------------------------------------
    // Simple result record
    // -------------------------------------------------------------------------

    public record RefreshResult(int newJobs, int totalSeen, LocalDateTime completedAt) {}
}
