package com.example.statetmachine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Demo application that drives the state machine through one of the valid paths.
 */
@SpringBootApplication
public class StateMachineApp implements CommandLineRunner {

    @Autowired
    private StateMachineFactory<DocumentState, DocumentEvent> factory;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApp.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("--- Starting State Machine Demo ---");

        StateMachine<DocumentState, DocumentEvent> sm = factory.getStateMachine("example-machine");
        sm.start();

        sm.sendEvent(DocumentEvent.HAS_DATA);
        sm.sendEvent(DocumentEvent.USER_AUTHORIZES);
        sm.sendEvent(DocumentEvent.RULES_PASS);

        System.out.println("Final State: " + sm.getState().getId());
    }
}
