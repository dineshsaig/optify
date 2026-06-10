package com.example.jobfinder.repository;

import com.example.jobfinder.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByApplyUrl(String applyUrl);

    // ── Listing queries (non-paginated, used by API) ──────────────────────────

    List<Job> findByIsNewTrueOrderByFirstSeenAtDesc();

    List<Job> findAllByOrderByFirstSeenAtDesc();

    List<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String company, String location);

    // ── Paginated queries (used by web UI) ────────────────────────────────────

    Page<Job> findByIsNewTrue(Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String company, String location, Pageable pageable);

    // ── Count helpers ─────────────────────────────────────────────────────────

    long countByIsNewTrue();

    // ── Bulk update ───────────────────────────────────────────────────────────

    @Modifying
    @Query("UPDATE Job j SET j.isNew = false")
    void clearAllNewFlags();
}
