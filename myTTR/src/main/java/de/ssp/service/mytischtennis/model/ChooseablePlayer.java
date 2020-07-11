package de.ssp.service.mytischtennis.model;

import android.graphics.drawable.Drawable;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;

public class ChooseablePlayer
{
    public static ArrayList<ChooseablePlayer> convertFromPlayers(ArrayList<Player> playerArrayList)
    {
        ArrayList<ChooseablePlayer> returnList = new ArrayList<>();
        if(playerArrayList != null && ! playerArrayList.isEmpty())
        {
            for (Player player: playerArrayList)
            {
                returnList.add(new ChooseablePlayer(player));
            }
        }
        return returnList;
    }

    public static ChooseablePlayer[] getPlayers(ArrayList<ChooseablePlayer> players)
    {
        return players.toArray(new ChooseablePlayer[players.size()]);
    }

    public Player player;
    public boolean isChecked;
    public Drawable playersAvatar;

    public ChooseablePlayer(Player player)
    {
        this.player = player;
    }

}
