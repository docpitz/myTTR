package com.jmelzer.myttr.logic;

/**
 * Created by cicgfp on 28.12.2017.
 */

public class LoginException extends Exception {
    private static final long serialVersionUID = 4870050673229952953L;

    String errorMessage;

    public LoginException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
