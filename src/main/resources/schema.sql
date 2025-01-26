-- Step 1: Create Condition Table
CREATE TABLE condition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
-- Step 2: Create Stage Table
CREATE TABLE workflow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Step 3: Create State Table (depends on Stage and Condition)
CREATE TABLE state (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Step 4: Create Transition Table (depends on State and Condition)
CREATE TABLE stage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state_id BIGINT NOT NULL,
    workflow_id BIGINT NOT NULL,
    CONSTRAINT fk_stage_state FOREIGN KEY (state_id) REFERENCES state(id),
    CONSTRAINT fk_stage_workflow FOREIGN KEY (workflow_id) REFERENCES workflow(id)
);

-- Step 5: Create Workflow Table (depends on Stage and State)
CREATE TABLE transition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_state_id BIGINT NOT NULL,
    to_state_id BIGINT NOT NULL,
    condition_id BIGINT NOT NULL,
    CONSTRAINT fk_transition_from_state FOREIGN KEY (from_state_id) REFERENCES state(id),
    CONSTRAINT fk_transition_to_state FOREIGN KEY (to_state_id) REFERENCES state(id),
    CONSTRAINT fk_transition_condition FOREIGN KEY (condition_id) REFERENCES condition(id)
);
