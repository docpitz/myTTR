package com.jmelzer.myttr.logic;

import java.net.UnknownHostException;

/**
 * Class for translate exceptions.
 * User: jmelzer
 * Date: 12.12.14
 * Time: 12:39
 */
public class NetworkException extends Exception {
    private static final long serialVersionUID = 7767176233436615324L;

    Throwable t;

    public NetworkException(Throwable throwable) {
        super(throwable);
        this.t = throwable;
    }

    public String getMessage() {
        return translate(t);
    }

    public static String translate(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "Konnte keine Verbindung zu mytischtennis.de herstellen";
        }
        return t.getMessage();
    }
}
