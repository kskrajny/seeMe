package com.skrajny.seeme;

public class Message {

    String message;
    String where;
    long deadline;

    public Message(String message, String where, long deadline) {
        this.message = message;
        this.where = where;
        this.deadline = deadline;
    }
}
