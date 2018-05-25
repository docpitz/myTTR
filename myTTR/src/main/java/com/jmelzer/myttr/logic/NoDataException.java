package com.jmelzer.myttr.logic;

/**
 * indicator if we see "Der Spieler ist noch keinem Team zugeordnet! " and other
 */
public class NoDataException extends Exception {

    private static final long serialVersionUID = 3194962466285200585L;

    public NoDataException(String msg) {
        super(msg);
    }
}
