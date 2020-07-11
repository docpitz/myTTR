package de.ssp.service.mytischtennis.caller;

public interface ServiceCaller {
    public void callService();
    public void cancelService();
    public boolean isServiceRunning();
}
