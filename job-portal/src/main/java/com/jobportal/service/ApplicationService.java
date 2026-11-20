package com.jobportal.service;

import com.jobportal.dto.Dtos.ApplicationResponse;
import com.jobportal.dto.Dtos.ApplyRequest;
import com.jobportal.exception.ConflictException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApplicationResponse apply(Long jobId, ApplyRequest req, String username) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
            throw new ConflictException("You have already applied to this job");
        }

        JobApplication app = new JobApplication();
        app.setJob(job);
        app.setApplicant(applicant);
        app.setCoverLetter(req == null ? null : req.coverLetter());
        return ApplicationResponse.from(applicationRepository.save(app));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> myApplications(String username) {
        return applicationRepository.findByApplicantUsernameOrderByAppliedAtDesc(username)
                .stream().map(ApplicationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> applicationsForJob(Long jobId, String employerUsername) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        if (!job.getPostedBy().getUsername().equals(employerUsername)) {
            throw new AccessDeniedException("Not your job posting");
        }
        return applicationRepository.findByJobIdOrderByAppliedAtDesc(jobId)
                .stream().map(ApplicationResponse::from).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status,
                                            String employerUsername) {
        JobApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
        if (!app.getJob().getPostedBy().getUsername().equals(employerUsername)) {
            throw new AccessDeniedException("Not your job posting");
        }
        app.setStatus(status);
        return ApplicationResponse.from(applicationRepository.save(app));
    }
}
