package com.example.statetmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Standard test for Spring State Machine 4.0.0 using non-reactive StateMachineFactory.
 */
@SpringBootTest
public class StateMachineTest {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    private StateMachine<DocumentState, DocumentEvent> sm;

    @BeforeEach
    void setUp() {
        sm = factory.getStateMachine("standard-machine");
        sm.start();
    }

    private void send(DocumentEvent event) {
        sm.sendEvent(MessageBuilder.withPayload(event).build());
    }

    @ParameterizedTest
    @CsvSource({
            "a,b,c,CREATED",
            "a,,c,REPAIR",
            "'','','',REPAIR",
            "foo,bar,baz,CREATED"
    })
    void testCheckDataRouting(String a, String b, String c, DocumentState expectedState) {
        sm.getExtendedState().getVariables().put("message", new MessageText(a, b, c));
        send(DocumentEvent.DATA_ENTERED);
        assertEquals(expectedState, sm.getState().getId());
    }

    @Test
    void testValidFlowToProcessed() {
        sm.getExtendedState().getVariables().put("message", new MessageText("a", "b", "c"));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    @Test
    void testRepairThenFixThenProcess() {
        sm.getExtendedState().getVariables().put("message", new MessageText("x", "", "z"));
        send(DocumentEvent.DATA_ENTERED);
        assertEquals(DocumentState.REPAIR, sm.getState().getId());

        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.SYSTEM_GENERATED);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    @Test
    void testRepairThenDelete() {
        sm.getExtendedState().getVariables().put("message", new MessageText("", "", ""));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.DELETED_VIA_GUI);
        assertEquals(DocumentState.DELETED, sm.getState().getId());
    }

    @Test
    void testRulesFail() {
        sm.getExtendedState().getVariables().put("message", new MessageText("1", "2", "3"));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_FAIL);
        assertEquals(DocumentState.AUTHORIZED, sm.getState().getId());
    }
}
