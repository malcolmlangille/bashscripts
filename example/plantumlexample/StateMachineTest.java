package com.example.statetmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for verifying all valid state machine flows and transitions.
 */
@SpringBootTest
public class StateMachineTest {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    private StateMachine<DocumentState, DocumentEvent> sm;

    /**
     * Creates and starts a fresh state machine before each test.
     */
    @BeforeEach
    void setUp() {
        sm = factory.getStateMachine("test-machine");
        sm.start();
    }

    /**
     * Simulates a normal case: data is complete, user authorizes it, and rules pass.
     * Expected path: MESSAGE_RECEIVED → CREATED → AUTHORIZED → PROCESSED
     */
    @Test
    void testHasDataFlow() {
        sm.sendEvent(DocumentEvent.HAS_DATA);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    /**
     * Simulates missing data that is repaired by user before authorization.
     * Expected path: MESSAGE_RECEIVED → REPAIR → CREATED → AUTHORIZED → PROCESSED
     */
    @Test
    void testMissingDataFlow() {
        sm.sendEvent(DocumentEvent.MISSING_DATA);
        sm.sendEvent(DocumentEvent.DATA_ENTERED);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    /**
     * Simulates auto-authorization by system without user input.
     * Expected path: MESSAGE_RECEIVED → CREATED → AUTHORIZED → PROCESSED
     */
    @Test
    void testSystemGeneratedFlow() {
        sm.sendEvent(DocumentEvent.HAS_DATA);
        sm.sendEvent(DocumentEvent.SYSTEM_GENERATED);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    /**
     * Simulates early deletion by user before data is repaired.
     * Expected path: MESSAGE_RECEIVED → REPAIR → DELETED
     */
    @Test
    void testDeleteFlow() {
        sm.sendEvent(DocumentEvent.MISSING_DATA);
        sm.sendEvent(DocumentEvent.DELETED_VIA_GUI);

        assertEquals(DocumentState.DELETED, sm.getState().getId());
    }

    /**
     * Simulates a case where business rules fail after authorization.
     * Expected path: MESSAGE_RECEIVED → CREATED → AUTHORIZED → AUTHORIZED
     */
    @Test
    void testRulesFailLoop() {
        sm.sendEvent(DocumentEvent.HAS_DATA);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_FAIL);

        assertEquals(DocumentState.AUTHORIZED, sm.getState().getId());
    }
}
