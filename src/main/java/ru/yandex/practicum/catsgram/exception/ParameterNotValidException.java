package ru.yandex.practicum.catsgram.exception;

public class ParameterNotValidException extends IllegalAccessException{
    final String parameter;
    final String reason;

    public ParameterNotValidException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }

    public String getParameter() {
        return parameter;
    }

    public String getReason() {
        return reason;
    }
}
