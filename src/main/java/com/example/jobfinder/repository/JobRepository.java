package com.example.jobfinder.repository;

import com.example.jobfinder.model.Job;
import com.example.jobfinder.model.Job.SponsorshipLikelihood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByApplyUrl(String applyUrl);

    List<Job> findByIsNewTrueOrderByFirstSeenAtDesc();

    List<Job> findAllByOrderByFirstSeenAtDesc();

    List<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String company, String location);

    List<Job> findBySponsorshipLikelihoodInOrderByFirstSeenAtDesc(
            List<SponsorshipLikelihood> likelihoods);

    /** Reset the isNew flag on all jobs before each refresh cycle. */
    @Modifying
    @Query("UPDATE Job j SET j.isNew = false")
    void clearAllNewFlags();
}
