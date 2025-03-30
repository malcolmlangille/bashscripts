package com.example.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

/**
 * Main application that demonstrates different scenarios through state machine transitions.
 */
@SpringBootApplication
public class StateMachineApp implements CommandLineRunner {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApp.class, args);
    }

    /**
     * Demonstrates four scenarios as described in the state diagram.
     */
    @Override
    public void run(String... args) {
        // Scenario 1: Missing data resolved by user via GUI
        System.out.println("\n--- Scenario 1: Missing data, resolved by user via GUI ---");
        StateMachine<DocumentState, DocumentEvent> machine1 = factory.getStateMachine("machine1");
        machine1.start();
        machine1.sendEvent(DocumentEvent.DATA_ENTERED);         // REPAIR -> CREATED
        machine1.sendEvent(DocumentEvent.USER_AUTHORIZES);      // CREATED -> AUTHORIZED
        machine1.sendEvent(DocumentEvent.RULES_PASS);           // AUTHORIZED -> PROCESSED
        machine1.stop();

        // Scenario 2: Already has data (start from CREATED)
        System.out.println("\n--- Scenario 2: Has data from start ---");
        StateMachine<DocumentState, DocumentEvent> machine2 = factory.getStateMachine("machine2");
        machine2.start();
        // Reset machine to CREATED manually
        machine2.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(
                org.springframework.statemachine.support.DefaultStateMachineContext.of(DocumentState.CREATED, null, null, null)));
        machine2.sendEvent(DocumentEvent.USER_AUTHORIZES);      // CREATED -> AUTHORIZED
        machine2.sendEvent(DocumentEvent.RULES_PASS);           // AUTHORIZED -> PROCESSED
        machine2.stop();

        // Scenario 3: System authorizes automatically
        System.out.println("\n--- Scenario 3: Authorized automatically by system ---");
        StateMachine<DocumentState, DocumentEvent> machine3 = factory.getStateMachine("machine3");
        machine3.start();
        machine3.sendEvent(DocumentEvent.DATA_ENTERED);         // REPAIR -> CREATED
        machine3.sendEvent(DocumentEvent.SYSTEM_GENERATED);     // CREATED -> AUTHORIZED
        machine3.sendEvent(DocumentEvent.RULES_PASS);           // AUTHORIZED -> PROCESSED
        machine3.stop();

        // Scenario 4: Authorized by user via GUI
        System.out.println("\n--- Scenario 4: Authorized manually by user ---");
        StateMachine<DocumentState, DocumentEvent> machine4 = factory.getStateMachine("machine4");
        machine4.start();
        machine4.sendEvent(DocumentEvent.DATA_ENTERED);         // REPAIR -> CREATED
        machine4.sendEvent(DocumentEvent.USER_AUTHORIZES);      // CREATED -> AUTHORIZED
        machine4.sendEvent(DocumentEvent.RULES_PASS);           // AUTHORIZED -> PROCESSED
        machine4.stop();
    }
}
