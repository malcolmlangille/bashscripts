package com.example.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineTest {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    private StateMachine<DocumentState, DocumentEvent> stateMachine;

    @BeforeEach
    void setUp() {
        // Each test will use a fresh machine instance
        stateMachine = factory.getStateMachine();
    }

    /** 
     * Test user fixing missing data via GUI, authorizing, and business rules passing 
     */
    @Test
    void testScenario1_userFixesDataAndAuthorizes() {
        stateMachine.start();
        stateMachine.sendEvent(DocumentEvent.DATA_ENTERED);
        stateMachine.sendEvent(DocumentEvent.USER_AUTHORIZES);
        stateMachine.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, stateMachine.getState().getId());
    }

    /** 
     * Test starting from CREATED and manually authorizing 
     */
    @Test
    void testScenario2_alreadyHasData() {
        stateMachine.start();
        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(
                org.springframework.statemachine.support.DefaultStateMachineContext.of(DocumentState.CREATED, null, null, null)
        ));

        stateMachine.sendEvent(DocumentEvent.USER_AUTHORIZES);
        stateMachine.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, stateMachine.getState().getId());
    }

    /** 
     * Test system auto-authorizing after data is entered 
     */
    @Test
    void testScenario3_systemAutoAuthorizes() {
        stateMachine.start();
        stateMachine.sendEvent(DocumentEvent.DATA_ENTERED);
        stateMachine.sendEvent(DocumentEvent.SYSTEM_GENERATED);
        stateMachine.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, stateMachine.getState().getId());
    }

    /** 
     * Test user manually authorizes after entering data 
     */
    @Test
    void testScenario4_userAuthorizesViaGUI() {
        stateMachine.start();
        stateMachine.sendEvent(DocumentEvent.DATA_ENTERED);
        stateMachine.sendEvent(DocumentEvent.USER_AUTHORIZES);
        stateMachine.sendEvent(DocumentEvent.RULES_PASS);

        assertEquals(DocumentState.PROCESSED, stateMachine.getState().getId());
    }

    /** 
     * Optional test: Deleting during repair should end in DELETED 
     */
    @Test
    void testDeleteFromRepair() {
        stateMachine.start();
        stateMachine.sendEvent(DocumentEvent.DELETED_VIA_GUI);

        assertEquals(DocumentState.DELETED, stateMachine.getState().getId());
    }

    /** 
     * Optional test: Business rules failing stays in AUTHORIZED 
     */
    @Test
    void testRulesFailStaysInAuthorized() {
        stateMachine.start();
        stateMachine.sendEvent(DocumentEvent.DATA_ENTERED);
        stateMachine.sendEvent(DocumentEvent.USER_AUTHORIZES);
        stateMachine.sendEvent(DocumentEvent.RULES_FAIL);

        assertEquals(DocumentState.AUTHORIZED, stateMachine.getState().getId());
    }
}
