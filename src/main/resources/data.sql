-- Insert data into Condition Table (Claim conditions)
INSERT INTO state (name) 
VALUES 
    ('Claim Initiated'), 
    ('Claim Under Review'), 
    ('Claim Approved');

INSERT INTO condition (name) 
VALUES 
    ('Review Pending'), 
    ('Under Investigation'), 
    ('Final Decision');

INSERT INTO workflow (name) 
VALUES 
    ('Claim Management Workflow');


INSERT INTO stage (name, state_id, workflow_id) 
VALUES 
    ('Initial Review', 1, 1), 
    ('Assessment', 2, 1), 
    ('Final Decision', 3, 1);
    
INSERT INTO transition (from_state_id, to_state_id, condition_id) 
VALUES 
    (1, 2, 1), -- From 'Claim Initiated' to 'Claim Under Review' with 'Review Pending' condition
    (2, 3, 2), -- From 'Claim Under Review' to 'Claim Approved' with 'Under Investigation' condition
    (3, 1, 3); -- From 'Claim Approved' to 'Claim Initiated' with 'Final Decision' condition



