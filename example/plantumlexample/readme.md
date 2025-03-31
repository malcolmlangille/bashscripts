1. Actions on Transitions
   You can attach Actions that execute logic when a transition occurs.

.withExternal()
.source(CREATED).target(AUTHORIZED).event(USER_AUTHORIZES)
.action(context -> System.out.println("User triggered authorization"))

🧱 2. Guards (Conditional Transitions)
Transitions can be conditionally allowed using Guards.

.withExternal()
.source(AUTHORIZED).target(PROCESSED).event(RULES_PASS)
.guard(context -> context.getExtendedState().get("valid", Boolean.class))

🧠 3. Choice States (Conditional Forking)
Allows transitions to multiple targets based on evaluation logic.

.withChoice()
.source(AUTHORIZED)
.first(PROCESSED, ctx -> businessRulesPass())
.last(AUTHORIZED)

🪝 4. Entry/Exit Actions
Run logic automatically when entering or exiting a state.

.states()
.stateEntry(AUTHORIZED, context -> System.out.println("Entered AUTHORIZED"))

🧰 5. State Machine Persistence
Store and resume the state machine state (e.g. in a database, Redis, etc.).

Useful in long-running processes or when integrating with distributed systems.

🧵 6. Hierarchical (Sub)States
States can be nested for modeling more complex workflows.

.withStates()
.state(REVIEW)
.and()
.withStates()
.parent(REVIEW)
.initial(UNDER_REVIEW)
.state(APPROVED)
.state(REJECTED)

🌐 7. Parallel (Region) States
Allows multiple states to be active at once in different regions.

Useful for tracking concurrent workflows or conditions.

🔄 8. Events with Payloads or Headers
You can send events with additional data using MessageBuilder.

Message<DocumentEvent> message = MessageBuilder
.withPayload(DocumentEvent.USER_AUTHORIZES)
.setHeader("userId", "alice123")
.build();

stateMachine.sendEvent(message);

🧭 9. Dynamic State Changes
You can programmatically change state mid-process (e.g. for resets or error handling).

stateMachine.getStateMachineAccessor()
.doWithAllRegions(access -> access.resetStateMachine(context));

🗂 10. StateMachineInterceptor
Hooks into pre/post transitions for logging, auditing, or even blocking transitions.

Test Coverage Summary
testValidFlowToProcessed	Valid data → CREATED → AUTHORIZED → PROCESSED
testRepairThenValidFlow	Missing field → REPAIR → user fixes → CREATED → AUTHORIZED → PROCESSED
testRepairThenDeleted	All fields missing → REPAIR → user deletes → DELETED
testRulesFail	Valid data → CREATED → AUTHORIZED → rules fail → stays in AUTHORIZED

