package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
           select j from Job j
           where (lower(j.title) like lower(concat('%', :keyword, '%'))
               or lower(j.description) like lower(concat('%', :keyword, '%'))
               or lower(j.companyName) like lower(concat('%', :keyword, '%')))
             and lower(j.location) like lower(concat('%', :location, '%'))
           """)
    Page<Job> search(@Param("keyword") String keyword,
                     @Param("location") String location,
                     Pageable pageable);

    List<Job> findByPostedByUsernameOrderByCreatedAtDesc(String username);
}
