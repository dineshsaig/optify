package com.example.jobfinder.controller;

import com.example.jobfinder.model.Job;
import com.example.jobfinder.repository.JobRepository;
import com.example.jobfinder.service.JobRefreshService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JobController {

    private final JobRepository        repository;
    private final JobRefreshService    refreshService;

    public JobController(JobRepository repository, JobRefreshService refreshService) {
        this.repository     = repository;
        this.refreshService = refreshService;
    }

    /**
     * Main dashboard.
     * Optional ?q= param filters by keyword across title, company, location.
     * Optional ?filter=new shows only jobs from the latest refresh.
     */
    @GetMapping({"/", "/jobs"})
    public String jobs(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String filter,
                       Model model) {

        List<Job> jobs;

        if (q != null && !q.isBlank()) {
            jobs = repository
                    .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
                            q, q, q);
            model.addAttribute("query", q);
        } else if ("new".equals(filter)) {
            jobs = repository.findByIsNewTrueOrderByFirstSeenAtDesc();
            model.addAttribute("filter", "new");
        } else {
            jobs = repository.findAllByOrderByFirstSeenAtDesc();
        }

        long newCount = repository.findByIsNewTrueOrderByFirstSeenAtDesc().size();

        model.addAttribute("jobs", jobs);
        model.addAttribute("newCount", newCount);
        model.addAttribute("total", repository.count());

        return "jobs";
    }

    /**
     * Trigger a manual refresh and redirect back to dashboard.
     */
    @GetMapping("/refresh")
    public String refresh(RedirectAttributes redirectAttrs) {
        JobRefreshService.RefreshResult result = refreshService.refresh();
        redirectAttrs.addFlashAttribute("message",
                String.format("Refresh complete: %d new jobs found (out of %d seen).",
                        result.newJobs(), result.totalSeen()));
        return "redirect:/jobs";
    }

    /**
     * Simple JSON endpoint for programmatic access or future React frontend.
     * GET /api/jobs?q=engineer&filter=new
     */
    @GetMapping("/api/jobs")
    @ResponseBody
    public List<Job> apiJobs(@RequestParam(required = false) String q,
                             @RequestParam(required = false) String filter) {
        if (q != null && !q.isBlank()) {
            return repository
                    .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
                            q, q, q);
        }
        if ("new".equals(filter)) {
            return repository.findByIsNewTrueOrderByFirstSeenAtDesc();
        }
        return repository.findAllByOrderByFirstSeenAtDesc();
    }

    /**
     * Trigger refresh via API (e.g. curl http://localhost:8080/api/refresh).
     */
    @GetMapping("/api/refresh")
    @ResponseBody
    public JobRefreshService.RefreshResult apiRefresh() {
        return refreshService.refresh();
    }
}
