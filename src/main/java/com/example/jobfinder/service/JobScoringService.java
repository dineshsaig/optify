package com.example.jobfinder.service;

import com.example.jobfinder.model.Job;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Lightweight keyword-based scoring for version 1.
 *
 * A job is "relevant" if its title contains at least one keyword
 * from the include list AND none from the exclude list.
 *
 * Adjust INCLUDE_KEYWORDS and EXCLUDE_KEYWORDS to match your search.
 */
@Service
public class JobScoringService {

    private static final List<String> INCLUDE_KEYWORDS = List.of(
            "software engineer",
            "software developer",
            "backend engineer",
            "frontend engineer",
            "full stack",
            "fullstack",
            "new grad",
            "junior",
            "intern",
            "entry level",
            "associate engineer"
    );

    private static final List<String> EXCLUDE_KEYWORDS = List.of(
            "senior",
            "staff",
            "principal",
            "director",
            "vp ",
            "manager",
            "lead"
    );

    /**
     * Returns true if the job title matches at least one include keyword
     * and no exclude keywords.
     */
    public boolean isRelevant(Job job) {
        String lower = job.getTitle().toLowerCase(Locale.ROOT);

        boolean hasInclude = INCLUDE_KEYWORDS.stream().anyMatch(lower::contains);
        if (!hasInclude) return false;

        boolean hasExclude = EXCLUDE_KEYWORDS.stream().anyMatch(lower::contains);
        return !hasExclude;
    }
}
