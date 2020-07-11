package de.ssp.service.mytischtennis.caller;

public interface ServiceFinish<S, F> {
    public void serviceFinished(S searchObject, boolean success, F returnObject, String errorMessage);
}
