package de.ssp.service.mytischtennis.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Speichert die Werte für die TTRKonstante
 * Automatisches Speichern und Auslesen der Werte aus den SharedPreferences
 */
public class TTRKonstante {

    private static final int TTR_KONSTANTE = 16;
    private static final int TTRKONSTANTE_ERHOEHUNG = 4;

    private static final String TTR_KONSTANTE_SAVE_NAME = "TTR_KONSTANTE";

    private static final String UEBER_EIN_JAHR_OHNE_SPIEL = "UEBER_EIN_JAHR_OHNE_SPIEL";
    private static final String WENIGER_ALS_15_SPIELE = "WENIGER_ALS_15_SPIELE";
    private static final String ALTER = "ALTER";

    private boolean ueberEinJahrOhneSpiel;
    private boolean wenigerAls15Spiele;
    private Alter alter;
    private Context context;

    private SharedPreferences ttrKonstanteSettings;

    /**
     * Konstruktor für TTRKonstante, lädt automatisch aus den Einstellungen die Werte aus.
     *
     * @param context
     */
    public TTRKonstante(Context context)
    {
        this.context = context;
    }

    public boolean getUeberEinJahrOhneSpiel()
    {
        updateFromPreferences();
        return ueberEinJahrOhneSpiel;
    }

    public boolean getWenigerAls15Spiele()
    {
        updateFromPreferences();
        return wenigerAls15Spiele;
    }

    public Alter getAlter() {
        updateFromPreferences();
        return alter;
    }

    /**
     * berechnet die Höhe der Änderungskonstante
     * @return die Änderungskonstante
     */
    public int getTTRKonstante ()
    {
        updateFromPreferences();
        int ttrKonstante = TTR_KONSTANTE;

        if (ueberEinJahrOhneSpiel)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        if (wenigerAls15Spiele)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        if (alter == Alter.UNTER_16)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG + TTRKONSTANTE_ERHOEHUNG;
        }
        if (alter == Alter.UNTER_21)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        return ttrKonstante;
    }

    public void setUeberEinJahrOhneSpiel(boolean ueberEinJahrOhneSpiel)
    {
        this.ueberEinJahrOhneSpiel = ueberEinJahrOhneSpiel;
        save();
    }

    public void setWenigerAls15Spiele(boolean wenigerAls15Spiele)
    {
        this.wenigerAls15Spiele = wenigerAls15Spiele;
        save();
    }

    public void setAlter(Alter alter)
    {
        this.alter = alter;
        save();
    }

    private void updateFromPreferences()
    {
        ttrKonstanteSettings = context.getSharedPreferences(TTR_KONSTANTE_SAVE_NAME, Context.MODE_PRIVATE);
        ueberEinJahrOhneSpiel = ttrKonstanteSettings.getBoolean(UEBER_EIN_JAHR_OHNE_SPIEL, false);
        wenigerAls15Spiele = ttrKonstanteSettings.getBoolean(WENIGER_ALS_15_SPIELE, false);
        alter = Alter.values()[ttrKonstanteSettings.getInt(ALTER, Alter.UNTER_16.ordinal())];
    }

    private void save()
    {
        SharedPreferences.Editor ttrKonstanteSettingsEditor = ttrKonstanteSettings.edit();
        ttrKonstanteSettingsEditor.putBoolean(UEBER_EIN_JAHR_OHNE_SPIEL ,ueberEinJahrOhneSpiel);
        ttrKonstanteSettingsEditor.putBoolean(WENIGER_ALS_15_SPIELE, wenigerAls15Spiele);
        ttrKonstanteSettingsEditor.putInt(ALTER, alter.ordinal());
        ttrKonstanteSettingsEditor.apply();
    }
}
