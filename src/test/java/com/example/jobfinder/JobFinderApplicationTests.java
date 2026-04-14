package com.example.jobfinder;

import com.example.jobfinder.model.CompanySource;
import com.example.jobfinder.model.Job;
import com.example.jobfinder.service.JobParseService;
import com.example.jobfinder.service.JobScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JobFinderApplicationTests {

    @Autowired
    JobParseService parseService;

    @Autowired
    JobScoringService scoringService;

    @Test
    void contextLoads() {
        // Verifies that all beans wire up correctly
    }

    @Test
    void parsesGreenhouseJsonFeed() {
        String json = """
                {
                  "jobs": [
                    {
                      "title": "Software Engineer, New Grad",
                      "location": { "name": "San Francisco, CA" },
                      "absolute_url": "https://boards.greenhouse.io/example/jobs/123",
                      "updated_at": "2024-04-01T00:00:00Z"
                    }
                  ]
                }
                """;

        CompanySource source = new CompanySource("TestCo",
                "https://boards-api.greenhouse.io/v1/boards/testco/jobs",
                CompanySource.SourceType.JSON);

        List<Job> jobs = parseService.parse(json, source);

        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getTitle()).isEqualTo("Software Engineer, New Grad");
        assertThat(jobs.get(0).getLocation()).isEqualTo("San Francisco, CA");
    }

    @Test
    void parsesLeverJsonFeed() {
        String json = """
                [
                  {
                    "text": "Backend Engineer",
                    "categories": { "location": "Remote" },
                    "hostedUrl": "https://jobs.lever.co/example/abc-123",
                    "createdAt": 1712000000000
                  }
                ]
                """;

        CompanySource source = new CompanySource("TestCo",
                "https://api.lever.co/v0/postings/testco?mode=json",
                CompanySource.SourceType.JSON);

        List<Job> jobs = parseService.parse(json, source);

        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void scoringFiltersOutSeniorRoles() {
        Job senior = new Job("Senior Software Engineer", "Acme", "NYC",
                "https://example.com/1", "Acme", "");
        Job newGrad = new Job("Software Engineer, New Grad", "Acme", "NYC",
                "https://example.com/2", "Acme", "");
        Job intern = new Job("Software Engineering Intern", "Acme", "NYC",
                "https://example.com/3", "Acme", "");

        assertThat(scoringService.isRelevant(senior)).isFalse();
        assertThat(scoringService.isRelevant(newGrad)).isTrue();
        assertThat(scoringService.isRelevant(intern)).isTrue();
    }
}
