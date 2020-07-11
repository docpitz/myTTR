package de.ssp.service.mytischtennis.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jmelzer.myttr.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Wettkampf
{
    private static final String TTR_WETTKAMPF_SAVE_NAME = "TTR_WETTKAMPF";

    private static final String MATCHES = "MATCHES";
    private static final String MEIN_TTR_WERT = "MEIN_TTR_WERT";
    private static final String MEIN_NAME = "MEIN_NAME";


    public ArrayList<Match> matches;
    public int meinTTRWert;
    public String meinName;
    private Context context;
    private SharedPreferences ttrWettkampfSettings;

    public Wettkampf(Context context)
    {
        this.context = context;
        load();
    }

    public void addMatches(List<Player> playerList)
    {
        if(matches.size() == 1 && matches.get(0).getGegnerischerTTRWert() == -1 && playerList.size() > 0)
        {
            matches.remove(0);
        }

        for (Player player: playerList)
        {
            Match match = new Match(player.getTtrPoints(), false, player.getFirstname() + " " + player.getLastname(), player.getClub());
            matches.add(match);
        }
    }

    public void save(ArrayList<Match> matches, int meinTTRWert)
    {
        this.matches = matches;
        this.meinTTRWert = meinTTRWert;
        ttrWettkampfSettings = context.getSharedPreferences(TTR_WETTKAMPF_SAVE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ttrWettkampfSettingsEditor = ttrWettkampfSettings.edit();
        Gson gson = new Gson();
        ttrWettkampfSettingsEditor.putString(MATCHES, gson.toJson(matches));
        ttrWettkampfSettingsEditor.putInt(MEIN_TTR_WERT, meinTTRWert);
        ttrWettkampfSettingsEditor.putString(MEIN_NAME, meinName);

        ttrWettkampfSettingsEditor.commit();
    }

    public void load()
    {
        ttrWettkampfSettings = context.getSharedPreferences(TTR_WETTKAMPF_SAVE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Match>>(){}.getType();
        String jsonMatches = ttrWettkampfSettings.getString(MATCHES, gson.toJson(new ArrayList<Match>()));
        matches = gson.fromJson(jsonMatches, listType);
        meinTTRWert = ttrWettkampfSettings.getInt(MEIN_TTR_WERT, -1);
        meinName = ttrWettkampfSettings.getString(MEIN_NAME, null);
    }
}
