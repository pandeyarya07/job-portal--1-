package com.jobportal.service;

import com.jobportal.dto.Dtos.JobRequest;
import com.jobportal.dto.Dtos.JobResponse;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository,
                      ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> search(String keyword, String location, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("createdAt").descending());
        return jobRepository.search(
                keyword == null ? "" : keyword.trim(),
                location == null ? "" : location.trim(),
                pageable).map(JobResponse::from);
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id) {
        return JobResponse.from(findJob(id));
    }

    @Transactional
    public JobResponse create(JobRequest req, String username) {
        User employer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = new Job();
        job.setPostedBy(employer);
        apply(job, req);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public JobResponse update(Long id, JobRequest req, String username) {
        Job job = findJob(id);
        checkOwner(job, username);
        apply(job, req);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public void delete(Long id, String username) {
        Job job = findJob(id);
        checkOwner(job, username);
        applicationRepository.deleteByJobId(id);
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> employerJobs(String username) {
        return jobRepository.findByPostedByUsernameOrderByCreatedAtDesc(username)
                .stream().map(JobResponse::from).toList();
    }

    // ----- helpers -----
    private Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
    }

    private void checkOwner(Job job, String username) {
        if (!job.getPostedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your job posting");
        }
    }

    private void apply(Job job, JobRequest req) {
        job.setTitle(req.title());
        job.setDescription(req.description());
        job.setCompanyName(req.companyName());
        job.setLocation(req.location());
        job.setSalary(req.salary());
    }
}
