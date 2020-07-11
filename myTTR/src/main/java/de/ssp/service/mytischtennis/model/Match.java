package de.ssp.service.mytischtennis.model;

public class Match
{
    public static int NO_MATCH = -1;
    private int gegnerischerTTRWert;
    private boolean gewonnen;
    private String nameVerein;

    public Match(int gegnerischerTTRWert, boolean gewonnen, String name, String verein)
    {
        this(gegnerischerTTRWert, gewonnen, getNameVerein(name, verein));
    }

    public Match(int gegnerischerTTRWert, boolean gewonnen, String nameVerein)
    {
        this.gegnerischerTTRWert = gegnerischerTTRWert;
        this.gewonnen = gewonnen;
        this.nameVerein = nameVerein;
    }

    public boolean isGewonnen() {
        return gewonnen;
    }

    public int getGegnerischerTTRWert() {
        return gegnerischerTTRWert;
    }

    public String getNameVerein()
    {
        return nameVerein;
    }

    protected static String getNameVerein(String name, String verein)
    {
        if(name != null && verein != null)
        {
            return name  + " (" + verein + ")";
        }
        else if(name != null && verein == null)
        {
            return name;
        }
        return null;
    }
}
