package com.example.statetmachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * State machine config defining all valid states, transitions, and listener.
 */
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<DocumentState, DocumentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<DocumentState, DocumentEvent> states) throws Exception {
        states
                .withStates()
                .initial(DocumentState.MESSAGE_RECEIVED)
                .states(EnumSet.allOf(DocumentState.class))
                .end(DocumentState.PROCESSED)
                .end(DocumentState.DELETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DocumentState, DocumentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(DocumentState.MESSAGE_RECEIVED).target(DocumentState.REPAIR).event(DocumentEvent.MISSING_DATA)
                .and().withExternal().source(DocumentState.MESSAGE_RECEIVED).target(DocumentState.CREATED).event(DocumentEvent.HAS_DATA)

                .and().withExternal().source(DocumentState.REPAIR).target(DocumentState.CREATED).event(DocumentEvent.DATA_ENTERED)
                .and().withExternal().source(DocumentState.REPAIR).target(DocumentState.DELETED).event(DocumentEvent.DELETED_VIA_GUI)

                .and().withExternal().source(DocumentState.CREATED).target(DocumentState.AUTHORIZED).event(DocumentEvent.SYSTEM_GENERATED)
                .and().withExternal().source(DocumentState.CREATED).target(DocumentState.AUTHORIZED).event(DocumentEvent.USER_AUTHORIZES)

                .and().withExternal().source(DocumentState.AUTHORIZED).target(DocumentState.AUTHORIZED).event(DocumentEvent.RULES_FAIL)
                .and().withExternal().source(DocumentState.AUTHORIZED).target(DocumentState.PROCESSED).event(DocumentEvent.RULES_PASS);
    }

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
