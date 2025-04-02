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
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        MessageText msg = new MessageText(a, b, c);
        sm.getExtendedState().getVariables().put("message", msg);
        send(DocumentEvent.DATA_ENTERED);
        assertEquals(expectedState, sm.getState().getId());
    }

    @Test
    void testValidFlowToProcessed() {
        MessageText msg = new MessageText("a", "b", "c");
        sm.getExtendedState().getVariables().put("message", msg);
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    @Test
    void testRepairThenFixThenProcess() {
        MessageText msg = new MessageText("x", "", "z");
        sm.getExtendedState().getVariables().put("message", msg);
        send(DocumentEvent.DATA_ENTERED);
        assertEquals(DocumentState.REPAIR, sm.getState().getId());

        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.SYSTEM_GENERATED);
        send(DocumentEvent.RULES_PASS);
        assertEquals(DocumentState.PROCESSED, sm.getState().getId());
    }

    @Test
    void testRepairThenDelete() {
        MessageText msg = new MessageText("", "", "");
        sm.getExtendedState().getVariables().put("message", msg);
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.DELETED_VIA_GUI);
        assertEquals(DocumentState.DELETED, sm.getState().getId());
    }

    @Test
    void testRulesFail() {
        MessageText msg = new MessageText("1", "2", "3");
        sm.getExtendedState().getVariables().put("message", msg);
        send(DocumentEvent.DATA_ENTERED);
        send(DocumentEvent.USER_AUTHORIZES);
        send(DocumentEvent.RULES_FAIL);
        assertEquals(DocumentState.AUTHORIZED, sm.getState().getId());
    }
}
