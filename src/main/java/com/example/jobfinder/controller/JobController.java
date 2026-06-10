package com.example.jobfinder.controller;

import com.example.jobfinder.model.Job;
import com.example.jobfinder.repository.JobRepository;
import com.example.jobfinder.service.JobRefreshService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JobController {

    private static final int PAGE_SIZE = 24;

    private final JobRepository     repository;
    private final JobRefreshService refreshService;

    public JobController(JobRepository repository, JobRefreshService refreshService) {
        this.repository     = repository;
        this.refreshService = refreshService;
    }

    /** Landing page */
    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("total",    repository.count());
        model.addAttribute("newCount", repository.countByIsNewTrue());
        return "index";
    }

    /** Job listing with search + filter + pagination */
    @GetMapping("/jobs")
    public String jobs(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String filter,
                       @RequestParam(defaultValue = "0")  int page,
                       Model model) {

        long newCount = repository.countByIsNewTrue();
        model.addAttribute("newCount", newCount);
        model.addAttribute("total",    repository.count());

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("firstSeenAt").descending());

        if (q != null && !q.isBlank()) {
            Page<Job> jobPage = repository
                    .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
                            q, q, q, pageable);
            model.addAttribute("jobs",        jobPage.getContent());
            model.addAttribute("currentPage", jobPage.getNumber());
            model.addAttribute("totalPages",  jobPage.getTotalPages());
            model.addAttribute("query", q);

        } else if ("new".equals(filter)) {
            Page<Job> jobPage = repository.findByIsNewTrue(pageable);
            model.addAttribute("jobs",        jobPage.getContent());
            model.addAttribute("currentPage", jobPage.getNumber());
            model.addAttribute("totalPages",  jobPage.getTotalPages());
            model.addAttribute("filter", "new");

        } else {
            Page<Job> jobPage = repository.findAll(pageable);
            model.addAttribute("jobs",        jobPage.getContent());
            model.addAttribute("currentPage", jobPage.getNumber());
            model.addAttribute("totalPages",  jobPage.getTotalPages());
        }

        return "jobs";
    }

    /** Individual job detail page */
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        Job job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        model.addAttribute("job",      job);
        model.addAttribute("newCount", repository.countByIsNewTrue());
        return "job";
    }

    /** Manual refresh → redirect back */
    @GetMapping("/refresh")
    public String refresh(RedirectAttributes redirectAttrs) {
        JobRefreshService.RefreshResult result = refreshService.refresh();
        redirectAttrs.addFlashAttribute("message",
                String.format("Refresh complete: %d new jobs found (out of %d seen).",
                        result.newJobs(), result.totalSeen()));
        return "redirect:/jobs";
    }

    /** JSON endpoint for programmatic access */
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

    /** Trigger refresh via API */
    @GetMapping("/api/refresh")
    @ResponseBody
    public JobRefreshService.RefreshResult apiRefresh() {
        return refreshService.refresh();
    }
}
