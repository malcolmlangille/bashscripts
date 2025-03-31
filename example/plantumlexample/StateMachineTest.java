package com.example.statetmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for verifying state machine flows and input-based branching using MessageText.
 */
@SpringBootTest
public class StateMachineTest {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    private StateMachine<DocumentState, DocumentEvent> sm;

    @BeforeEach
    void setUp() {
        sm = factory.getStateMachine("param-test-machine");
        sm.start();
    }

    /**
     * Parameterized test to verify whether MessageText routes to CREATED or REPAIR.
     */
    @ParameterizedTest
    @CsvSource({
            "a,b,c,CREATED",      // all fields present
            "a,,c,REPAIR",        // missing field b
            "'','','',REPAIR",    // all missing
            "foo,bar,baz,CREATED" // valid values
    })
    void testCheckDataRouting(String a, String b, String c, DocumentState expectedState) {
        MessageText msg = new MessageText(a, b, c);
        sm.getExtendedState().getVariables().put("message", msg);
        sm.sendEvent(DocumentEvent.DATA_ENTERED);

        assertEquals(expectedState, sm.getState().getId());
    }

    /**
     * Valid input flow: CREATED → AUTHORIZED → PROCESSED
     */
    @Test
    void testValidFlowToProcessed() {
        MessageText msg = new MessageText("a", "b", "c");
        sm.getExtendedState().getVariables().put("message", msg);
        sm.sendEvent(DocumentEvent.DATA_ENTERED);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    /**
     * Repair path followed by fix and completion.
     */
    @Test
    void testRepairThenFixThenProcess() {
        MessageText msg = new MessageText("x", "", "z");
        sm.getExtendedState().getVariables().put("message", msg);
        sm.sendEvent(DocumentEvent.DATA_ENTERED); // goes to REPAIR
        assertEquals(DocumentState.REPAIR, sm.getState().getId());

        // Simulate user entering missing data and system auto-authorizes
        sm.sendEvent(DocumentEvent.DATA_ENTERED);
        sm.sendEvent(DocumentEvent.SYSTEM_GENERATED);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    /**
     * Test deletion during repair.
     */
    @Test
    void testRepairThenDelete() {
        MessageText msg = new MessageText("", "", "");
        sm.getExtendedState().getVariables().put("message", msg);
        sm.sendEvent(DocumentEvent.DATA_ENTERED); // goes to REPAIR
        sm.sendEvent(DocumentEvent.DELETED_VIA_GUI); // deleted

        assertEquals(DocumentState.DELETED, sm.getState().getId());
    }

    /**
     * Rules fail after authorization: should remain in AUTHORIZED.
     */
    @Test
    void testRulesFail() {
        MessageText msg = new MessageText("1", "2", "3");
        sm.getExtendedState().getVariables().put("message", msg);
        sm.sendEvent(DocumentEvent.DATA_ENTERED);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_FAIL);

        assertEquals(DocumentState.AUTHORIZED, sm.getState().getId());
    }
}
