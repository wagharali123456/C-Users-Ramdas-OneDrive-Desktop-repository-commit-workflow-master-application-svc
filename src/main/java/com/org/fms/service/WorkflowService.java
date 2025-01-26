package com.org.fms.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.fms.model.Condition;
import com.org.fms.model.Stage;
import com.org.fms.model.State;
import com.org.fms.model.Transition;
import com.org.fms.model.Workflow;
import com.org.fms.repository.ConditionRepository;
import com.org.fms.repository.StageRepository;
import com.org.fms.repository.StateRepository;
import com.org.fms.repository.TransitionRepository;
import com.org.fms.repository.WorkflowRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private TransitionRepository transitionRepository;

    @Autowired
    private ConditionRepository conditionRepository;

    // Initialize the workflow
    public Workflow initWorkflow(Long workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found for ID: " + workflowId));
    }

    public Stage translateStage(Long workflowId, Long currentStateId, Long conditionId) {
        // Validate inputs
        validateInputs(workflowId, currentStateId, conditionId);

        // Fetch necessary data
        Workflow workflow = fetchWorkflow(workflowId);
        State currentState = fetchState(currentStateId);
        Condition condition = fetchCondition(conditionId);

        // Fetch valid transitions based on currentState and conditionId
        Transition transition = getValidTransition(currentState, condition);
        State nextState = transition.getToState();

        // Find the next stage based on the next state
        Stage nextStage = getNextStage(workflowId, nextState);

        // Update and save the workflow
        workflow.setCurrentState(nextState);
        workflow.setCurrentStage(nextStage);
        workflowRepository.save(workflow);

        return nextStage;
    }

    private void validateInputs(Long workflowId, Long currentStateId, Long conditionId) {
        if (workflowId == null || workflowId <= 0 || currentStateId == null || currentStateId <= 0 || conditionId == null || conditionId <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
    }

    private Workflow fetchWorkflow(Long workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found for ID: " + workflowId));
    }

    private State fetchState(Long currentStateId) {
        return stateRepository.findById(currentStateId)
                .orElseThrow(() -> new RuntimeException("State not found for ID: " + currentStateId));
    }

    private Condition fetchCondition(Long conditionId) {
        return conditionRepository.findById(conditionId)
                .orElseThrow(() -> new RuntimeException("Condition not found for ID: " + conditionId));
    }

    private Transition getValidTransition(State currentState, Condition condition) {
        return transitionRepository.findByFromStateAndCondition(currentState, condition)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No valid transition found for state: " + currentState.getName() + " and condition: " + condition.getName()));
    }

    private Stage getNextStage(Long workflowId, State nextState) {
        List<Stage> stages = stageRepository.findByWorkflowId(workflowId);
        return stages.stream()
                .filter(stage -> stage.getState() != null && stage.getState().equals(nextState))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Stage not found for state: " + nextState.getName()));
    }

    public Map<String, Object> getCurrentStageAndState(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found for ID: " + workflowId));

        Stage currentStage = workflow.getCurrentStage();
        State currentState = workflow.getCurrentState();

        if (currentStage == null || currentState == null) {
            return Map.of("status", "error", "message", "No current stage or state found for workflow ID: " + workflowId);
        }

        return Map.of(
            "currentStageId", currentStage.getId(),
            "currentStateId", currentState.getId(),
            "currentStage", currentStage.getName(),
            "currentState", currentState.getName()
        );
    }
}
