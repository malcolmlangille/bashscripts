package com.example.statetmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ReactiveStateMachine;
import org.springframework.statemachine.config.ReactiveStateMachineFactory;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reactive test for Spring State Machine 4.0.0 using ReactiveStateMachineFactory.
 */
@SpringBootTest
public class StateMachineTest {

    @Autowired
    private ReactiveStateMachineFactory<DocumentState, DocumentEvent> factory;

    private ReactiveStateMachine<DocumentState, DocumentEvent> sm;

    @BeforeEach
    void setUp() {
        sm = factory.getStateMachine("reactive-machine");
        sm.getExtendedState().getVariables().put("message", new MessageText("a", "b", "c")); // default
        sm.start().block();
    }

    private void send(DocumentEvent event) {
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).block();
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
        assertEquals(expectedState, sm.getState().block().getId());
    }

    @Test
    void testValidFlowToProcessed() {
        sm.getExtendedState().getVariables().put("message", new MessageText("a", "b", "c"));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().block().getId());
    }

    @Test
    void testRepairThenFixThenProcess() {
        sm.getExtendedState().getVariables().put("message", new MessageText("x", "", "z"));
        send(DocumentEvent.DATA_ENTERED);
        assertEquals(DocumentState.REPAIR, sm.getState().block().getId());

        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.SYSTEM_GENERATED);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().block().getId());
    }

    @Test
    void testRepairThenDelete() {
        sm.getExtendedState().getVariables().put("message", new MessageText("", "", ""));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.DELETED_VIA_GUI);
        assertEquals(DocumentState.DELETED, sm.getState().block().getId());
    }

    @Test
    void testRulesFail() {
        sm.getExtendedState().getVariables().put("message", new MessageText("1", "2", "3"));
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_FAIL);
        assertEquals(DocumentState.AUTHORIZED, sm.getState().block().getId());
    }
}
