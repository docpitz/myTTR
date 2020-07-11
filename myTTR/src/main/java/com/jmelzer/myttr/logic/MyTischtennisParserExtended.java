package com.jmelzer.myttr.logic;

import java.util.HashMap;
import java.util.Map;

public class MyTischtennisParserExtended extends MyTischtennisParser
{
    private static Map<String, String> adresses = new HashMap<>();
    public static String AVATAR_MAN_FALLBACK = "https://www.mytischtennis.de/community/images/user_images_placeholder/NoAvatar_Boy120.jpg";

    public String getPicUrl(String playersId) throws NetworkException, LoginExpiredException, NiceGuysException
    {
        String adresse = adresses.get(playersId);
        if(adresse == null)
        {
            adresse = getPicUrlFromRemote(playersId);
            adresses.put(playersId, adresse);
        }
        return adresse;
    }

    private String getPicUrlFromRemote(String playersId) throws NetworkException, LoginExpiredException, NiceGuysException
    {
        String url = "https://www.mytischtennis.de/community/ajax/_tooltippstuff?otherUsersClickTTId=" + playersId;
        String page = Client.getPage(url);

        ParseResult parseResult = readBetween(page, 0, "<img src=\"", "\" class=\"user-image\">");
        if(parseResult == null || parseResult.isEmpty())
        {
            return AVATAR_MAN_FALLBACK;
        }
        return "https://www.mytischtennis.de" + parseResult.result;
    }
}

