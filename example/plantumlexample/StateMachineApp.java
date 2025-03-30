package com.example.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

@SpringBootApplication
public class StateMachineApp implements CommandLineRunner {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApp.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n--- Scenario 1: Missing data, resolved by user via GUI ---");
        StateMachine<DocumentState, DocumentEvent> machine1 = factory.getStateMachine("machine1");
        machine1.start();
        machine1.sendEvent(DocumentEvent.DATA_ENTERED); // Repair -> Created
        machine1.sendEvent(DocumentEvent.USER_AUTHORIZES); // Created -> Authorized
        machine1.sendEvent(DocumentEvent.RULES_PASS); // Authorized -> Processed
        machine1.stop();

        System.out.println("\n--- Scenario 2: Has data from start ---");
        StateMachine<DocumentState, DocumentEvent> machine2 = factory.getStateMachine("machine2");
        machine2.start();
        machine2.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(org.springframework.statemachine.support.DefaultStateMachineContext.of(DocumentState.CREATED, null, null, null)));
        machine2.sendEvent(DocumentEvent.USER_AUTHORIZES);
        machine2.sendEvent(DocumentEvent.RULES_PASS);
        machine2.stop();

        System.out.println("\n--- Scenario 3: Authorized automatically by system ---");
        StateMachine<DocumentState, DocumentEvent> machine3 = factory.getStateMachine("machine3");
        machine3.start();
        machine3.sendEvent(DocumentEvent.DATA_ENTERED);
        machine3.sendEvent(DocumentEvent.SYSTEM_GENERATED);
        machine3.sendEvent(DocumentEvent.RULES_PASS);
        machine3.stop();

        System.out.println("\n--- Scenario 4: Authorized manually by user ---");
        StateMachine<DocumentState, DocumentEvent> machine4 = factory.getStateMachine("machine4");
        machine4.start();
        machine4.sendEvent(DocumentEvent.DATA_ENTERED);
        machine4.sendEvent(DocumentEvent.USER_AUTHORIZES);
        machine4.sendEvent(DocumentEvent.RULES_PASS);
        machine4.stop();
    }
}
