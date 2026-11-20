package com.jobportal.dto;

import com.jobportal.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Dtos {

    // ---------- Requests ----------
    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 30) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotNull Role role) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {}

    public record JobRequest(
            @NotBlank String title,
            @NotBlank @Size(max = 4000) String description,
            @NotBlank String companyName,
            @NotBlank String location,
            @PositiveOrZero Double salary) {}

    public record ApplyRequest(@Size(max = 4000) String coverLetter) {}

    public record StatusUpdateRequest(@NotNull ApplicationStatus status) {}

    // ---------- Responses ----------
    public record AuthResponse(String token, String username, String email, Role role) {}

    public record UserResponse(Long id, String username, String email, Role role) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        }
    }

    public record JobResponse(Long id, String title, String description, String companyName,
                              String location, Double salary, LocalDateTime createdAt,
                              String postedBy) {
        public static JobResponse from(Job j) {
            return new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getCompanyName(),
                    j.getLocation(), j.getSalary(), j.getCreatedAt(), j.getPostedBy().getUsername());
        }
    }

    public record ApplicationResponse(Long id, Long jobId, String jobTitle,
                                      String applicantUsername, String applicantEmail,
                                      String coverLetter, ApplicationStatus status,
                                      LocalDateTime appliedAt) {
        public static ApplicationResponse from(JobApplication a) {
            return new ApplicationResponse(a.getId(), a.getJob().getId(), a.getJob().getTitle(),
                    a.getApplicant().getUsername(), a.getApplicant().getEmail(),
                    a.getCoverLetter(), a.getStatus(), a.getAppliedAt());
        }
    }
}
