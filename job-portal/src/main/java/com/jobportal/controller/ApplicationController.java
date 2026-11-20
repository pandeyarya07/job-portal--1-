package com.jobportal.controller;

import com.jobportal.dto.Dtos.ApplicationResponse;
import com.jobportal.dto.Dtos.ApplyRequest;
import com.jobportal.dto.Dtos.StatusUpdateRequest;
import com.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // ----- job seeker -----
    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@PathVariable Long jobId,
                                     @Valid @RequestBody(required = false) ApplyRequest req,
                                     Authentication auth) {
        return applicationService.apply(jobId, req, auth.getName());
    }

    @GetMapping("/applications/mine")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public List<ApplicationResponse> mine(Authentication auth) {
        return applicationService.myApplications(auth.getName());
    }

    // ----- employer -----
    @GetMapping("/employer/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<ApplicationResponse> forJob(@PathVariable Long jobId, Authentication auth) {
        return applicationService.applicationsForJob(jobId, auth.getName());
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApplicationResponse updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest req,
                                            Authentication auth) {
        return applicationService.updateStatus(id, req.status(), auth.getName());
    }
}
