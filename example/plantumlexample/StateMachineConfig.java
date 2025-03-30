package com.example.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Configuration class for the state machine using enums for states and events.
 */
@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<DocumentState, DocumentEvent> {

    /**
     * Defines the possible states and sets the initial and terminal states.
     */
    @Override
    public void configure(StateMachineStateConfigurer<DocumentState, DocumentEvent> states) throws Exception {
        states
                .withStates()
                .initial(DocumentState.REPAIR) // Start here if data is missing
                .states(EnumSet.allOf(DocumentState.class)) // All defined states
                .end(DocumentState.PROCESSED) // Final state
                .end(DocumentState.DELETED);  // Alternative final state
    }

    /**
     * Defines all the allowed transitions between states based on specific events.
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<DocumentState, DocumentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(DocumentState.REPAIR).target(DocumentState.CREATED).event(DocumentEvent.DATA_ENTERED)
                // User enters missing data via GUI
                .and().withExternal()
                .source(DocumentState.REPAIR).target(DocumentState.DELETED).event(DocumentEvent.DELETED_VIA_GUI)
                // User deletes document
                .and().withExternal()
                .source(DocumentState.CREATED).target(DocumentState.AUTHORIZED).event(DocumentEvent.SYSTEM_GENERATED)
                // System authorizes automatically
                .and().withExternal()
                .source(DocumentState.CREATED).target(DocumentState.AUTHORIZED).event(DocumentEvent.USER_AUTHORIZES)
                // User authorizes manually
                .and().withExternal()
                .source(DocumentState.AUTHORIZED).target(DocumentState.AUTHORIZED).event(DocumentEvent.RULES_FAIL)
                // Business rules failed, remain in same state
                .and().withExternal()
                .source(DocumentState.AUTHORIZED).target(DocumentState.PROCESSED).event(DocumentEvent.RULES_PASS);
        // Business rules passed, move to processed
    }

    /**
     * Adds a listener to log state changes for debugging and illustration.
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<DocumentState, DocumentEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineListenerAdapter<>() {
                    @Override
                    public void stateChanged(State<DocumentState, DocumentEvent> from, State<DocumentState, DocumentEvent> to) {
                        System.out.printf("State change from %s to %s%n",
                                from == null ? "none" : from.getId(), to.getId());
                    }
                });
    }
}
