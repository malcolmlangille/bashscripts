package com.example.statemachine;

public enum DocumentEvent {
    HAS_DATA,
    MISSING_DATA,
    DATA_ENTERED,
    DELETED_VIA_GUI,
    SYSTEM_GENERATED,
    USER_AUTHORIZES,
    RULES_PASS,
    RULES_FAIL
}
