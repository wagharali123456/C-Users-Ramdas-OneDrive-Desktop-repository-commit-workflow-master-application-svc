package com.org.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.fms.model.Workflow;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    // Add any custom query methods if required
}
