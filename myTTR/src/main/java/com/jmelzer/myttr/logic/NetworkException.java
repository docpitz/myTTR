package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Constants;

import java.net.UnknownHostException;

/**
 * Class for translate exceptions.
 * User: jmelzer
 * Date: 12.12.14
 * Time: 12:39
 */
public class NetworkException extends Exception {
    private static final long serialVersionUID = 7767176233436615324L;
    private int code;

    Throwable t;

    public NetworkException(int code) {
        this.code = code;
    }

    public NetworkException(Throwable throwable) {
        super(throwable);
        this.t = throwable;
    }

    public String getMessage() {
        if (code > 200) {
            return String.format(Constants.MYTT_ERROR, code);
        }
        return translate(t);
    }

    public static String translate(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "Konnte keine Verbindung zum Server herstellen";
        }
        return t.getMessage();
    }
}
