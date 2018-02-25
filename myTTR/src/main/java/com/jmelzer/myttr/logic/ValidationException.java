package com.jmelzer.myttr.logic;

/**
 * Created by jmelzer on 20.02.2018.
 */

public class ValidationException extends Exception {
    private static final long serialVersionUID = -6670809074732374939L;

    public ValidationException(String message) {
        super(message);
    }
}
