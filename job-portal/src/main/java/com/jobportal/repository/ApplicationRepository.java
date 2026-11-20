package com.jobportal.repository;

import com.jobportal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    List<JobApplication> findByApplicantUsernameOrderByAppliedAtDesc(String username);
    List<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId);
    void deleteByJobId(Long jobId);
}
