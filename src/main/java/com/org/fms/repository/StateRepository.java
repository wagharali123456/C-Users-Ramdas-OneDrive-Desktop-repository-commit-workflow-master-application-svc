package com.org.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.fms.model.State;

public interface StateRepository extends JpaRepository<State, Long> {
    //List<State> findByStageId(Long stageId);
}
