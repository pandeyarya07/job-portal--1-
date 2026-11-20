package com.jobportal.controller;

import com.jobportal.dto.Dtos.JobRequest;
import com.jobportal.dto.Dtos.JobResponse;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ----- public -----
    @GetMapping("/jobs")
    public Page<JobResponse> search(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return jobService.search(keyword, location, page, size);
    }

    @GetMapping("/jobs/{id}")
    public JobResponse get(@PathVariable Long id) {
        return jobService.get(id);
    }

    // ----- employer only -----
    @PostMapping("/jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@Valid @RequestBody JobRequest req, Authentication auth) {
        return jobService.create(req, auth.getName());
    }

    @PutMapping("/jobs/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest req,
                              Authentication auth) {
        return jobService.update(id, req, auth.getName());
    }

    @DeleteMapping("/jobs/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        jobService.delete(id, auth.getName());
    }

    @GetMapping("/employer/jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<JobResponse> myJobs(Authentication auth) {
        return jobService.employerJobs(auth.getName());
    }
}
