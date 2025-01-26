package com.org.fms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.fms.model.Condition;
import com.org.fms.model.State;
import com.org.fms.model.Transition;

public interface TransitionRepository extends JpaRepository<Transition, Long> {
   // List<Transition> findByFromStateAndConditionId(Long fromStateId, Long conditionId);
    public List<Transition> findByFromStateAndCondition(State state, Condition condition);
}
