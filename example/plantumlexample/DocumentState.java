package com.example.statemachine;

/**
 * Defines the possible states a document can be in.
 */
public enum DocumentState {
    CREATED,
    REPAIR,
    AUTHORIZED,
    PROCESSED,
    DELETED
}
