package com.example.statetmachine;

/**
 * Defines the possible states a document can be in.
 */
public enum DocumentState {
    MESSAGE_RECEIVED,
    REPAIR,
    CREATED,
    AUTHORIZED,
    PROCESSED,
    DELETED
}
