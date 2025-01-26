package com.org.fms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.org.fms.model.Stage;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findByWorkflowId(Long workflowId);
    @Query("SELECT s FROM Stage s JOIN FETCH s.state WHERE s.workflow.id = :workflowId")
    List<Stage> findByWorkflowIdWithState(Long workflowId);
}
