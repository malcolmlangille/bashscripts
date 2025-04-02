package com.example.statetmachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<DocumentState, DocumentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<DocumentState, DocumentEvent> states) throws Exception {
        states
                .withStates()
                .initial(DocumentState.MESSAGE_RECEIVED)
                .choice("CHECK_DATA")
                .states(EnumSet.allOf(DocumentState.class))
                .end(DocumentState.PROCESSED)
                .end(DocumentState.DELETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DocumentState, DocumentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(DocumentState.MESSAGE_RECEIVED).target("CHECK_DATA").event(DocumentEvent.DATA_ENTERED)
                .and().withChoice()
                .source("CHECK_DATA")
                .first(DocumentState.CREATED, hasValidData())
                .last(DocumentState.REPAIR)
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

    private Guard<DocumentState, DocumentEvent> hasValidData() {
        return context -> {
            MessageText message = context.getExtendedState().get("message", MessageText.class);
            return message != null && message.hasAllFields();
        };
    }
}
