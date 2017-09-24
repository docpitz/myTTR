package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 01.04.2015.
 * data for notifications.
 */
public class LastNotification {
    public static final String EVENT_TYPE = "events";
    public static final String VERSION_TYPE = "version";
    Date changedAt;
    String type;
    String jsonData;

    public LastNotification(Date changedAt, String type, String jsonData) {
        this.changedAt = changedAt;
        this.type = type;
        this.jsonData = jsonData;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public String getType() {
        return type;
    }

    public String getJsonData() {
        return jsonData;
    }

    public static String convertToJson(List<Event> events, int ttr) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("events", events.size());
        jsonObject.put("ttr", ttr);
        return jsonObject.toString();
    }

    public int convertTTRFromJson() throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getInt("ttr");
    }
    public int convertEventSizeFromJson() throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getInt("events");
    }
}
