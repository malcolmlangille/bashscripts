package com.example.statetmachine;

public class MessageText {
    private String a;
    private String b;
    private String c;

    public MessageText(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public boolean hasAllFields() {
        return a != null && !a.isBlank() &&
               b != null && !b.isBlank() &&
               c != null && !c.isBlank();
    }
}
