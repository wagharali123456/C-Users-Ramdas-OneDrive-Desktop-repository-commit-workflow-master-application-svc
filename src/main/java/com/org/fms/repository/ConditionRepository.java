package com.org.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.org.fms.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
