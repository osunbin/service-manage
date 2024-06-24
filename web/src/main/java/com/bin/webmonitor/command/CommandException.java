package com.bin.webmonitor.command;

/**
 *  指令异常类
 */
public class CommandException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private String code;

    public CommandException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommandCenterException{");
        sb.append("code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

