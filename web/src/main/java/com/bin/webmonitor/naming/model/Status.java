package com.bin.webmonitor.naming.model;

import java.util.Objects;

public class Status {
    private int code;
    private String message;

    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public Status setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Status setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Status status = (Status)o;
        return code == status.code && Objects.equals(message, status.message);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, message);
    }
}
