package com.org.fms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.fms.model.Stage;
import com.org.fms.model.Workflow;
import com.org.fms.service.WorkflowService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    // Endpoint to initialize a workflow and return its stages, state, and workflow details
    @PostMapping("/init/{workflowId}")
    public ResponseEntity<Map<String, Object>> initWorkflow(@PathVariable Long workflowId) {
        if (workflowId <= 0) {
            return (ResponseEntity<Map<String, Object>>) buildErrorResponse("Invalid workflow ID: " + workflowId, HttpStatus.BAD_REQUEST);
        }

        Workflow workflow = workflowService.initWorkflow(workflowId);
        if (workflow == null) {
            return buildErrorResponse("Workflow not found for id: " + workflowId, HttpStatus.NOT_FOUND);
        }

        // Build the response with workflow details and the first stage's state
        Map<String, Object> successResponse = prepareWorkflowResponse(workflow);
        return ResponseEntity.ok(successResponse);
    }

    // Endpoint to translate a stage based on the current state and condition
    @PostMapping("/translate/{workflowId}/{currentStateId}/{conditionId}")
    public ResponseEntity<Map<String, Object>> translateStage(@PathVariable Long workflowId,
                                                              @PathVariable Long currentStateId,
                                                              @PathVariable Long conditionId) {
        // Validate input parameters
        if (workflowId <= 0 || currentStateId <= 0 || conditionId <= 0) {
            return buildErrorResponse("Invalid input parameters", HttpStatus.BAD_REQUEST);
        }

        try {
            // Call the service method to translate the stage based on the condition
            Stage nextStage = workflowService.translateStage(workflowId, currentStateId, conditionId);

            // Prepare the response with the updated stage details
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Stage translation successful");
            successResponse.put("nextStage", nextStage != null ? nextStage.getName() : "Unknown");
            successResponse.put("nextStageId", nextStage != null && nextStage.getId() != null ? nextStage.getId() : "Unknown");
            successResponse.put("nextState", nextStage != null && nextStage.getState() != null ? nextStage.getState().getName() : "Unknown");
            successResponse.put("nextStateId", nextStage != null && nextStage.getState() != null && nextStage.getState().getId() != null
                    ? nextStage.getState().getId() : "Unknown");

            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            //log.error("Error occurred while translating stage", e);
            return buildErrorResponse("Error occurred while translating stage", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Endpoint to fetch the current stage and state for a given workflow
    @PostMapping("/status/{workflowId}")
    public ResponseEntity<Map<String, Object>> getCurrentStageAndState(@PathVariable Long workflowId) {
        if (workflowId <= 0) {
            return buildErrorResponse("Invalid workflow ID: " + workflowId, HttpStatus.BAD_REQUEST);
        }

        try {
            Map<String, Object> status = workflowService.getCurrentStageAndState(workflowId);

            if (status == null || status.isEmpty()) {
                return buildErrorResponse("No current stage and state found for workflowId: " + workflowId, HttpStatus.NOT_FOUND);
            }

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Current stage and state fetched successfully");
            successResponse.put("data", status);
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            //log.error("Error occurred while fetching current stage and state", e);
            return buildErrorResponse("Error occurred while fetching current stage and state", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Helper method to build a standard error response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Helper method to build a detailed error response with specific error details
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, String errorDetails) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        errorResponse.put("error", errorDetails);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Helper method to prepare the workflow initialization response
    private Map<String, Object> prepareWorkflowResponse(Workflow workflow) {
        Stage firstStage = workflow.getStages() != null && !workflow.getStages().isEmpty() ? workflow.getStages().get(0) : null;
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Workflow initialization successful");
        response.put("workflowName", workflow.getName());
        response.put("workflowId", workflow.getId());
        response.put("stageName", firstStage != null ? firstStage.getName() : "N/A");
        response.put("stateName", firstStage != null && firstStage.getState() != null ? firstStage.getState().getName() : "N/A");
        return response;
    }
}
