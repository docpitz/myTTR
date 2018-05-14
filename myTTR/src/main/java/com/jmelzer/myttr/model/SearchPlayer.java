package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Club;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

/**
 * Created by J. Melzer on 28.09.2017.
 * Object for storing infos about player to search fpr mytt
 */

public class SearchPlayer implements Serializable, Favorite {
    private static final long serialVersionUID = 3253568223402780761L;
    String lastname;
    String firstname;
    Club club;
    String gender;
    int yearFrom = -1;
    int yearTo = -1;
    int ttrFrom = -1;
    int ttrTo = -1;
    //name of the search for persistence
    private String name;
    //url of the search for persistence
    private String url;
    private Date changedAt;
    boolean actual = true;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public int getTtrFrom() {
        return ttrFrom;
    }

    public void setTtrFrom(int ttrFrom) {
        this.ttrFrom = ttrFrom;
    }

    public int getTtrTo() {
        return ttrTo;
    }

    public void setTtrTo(int ttrTo) {
        this.ttrTo = ttrTo;
    }

    public String getClubName() {
        if (club != null)
            return club.getName();
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setChangedAt(Date d) {
        this.changedAt = d;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String typeForMenu() {
        return "Suche";
    }

    public String createName() {
        String name = "";
        if (isNoneEmpty(firstname, lastname)) {
            name = add(name, firstname);
            name = add(name, lastname);
        }
        if (club != null) {
            name = add(name, club.getName());
        }

        if (ttrFrom > 0 && ttrTo > 0) {
            name += " (" + ttrFrom + "-" + ttrTo + ")";
        } else {
            if (ttrFrom > 0) {
                name += " (" + ttrFrom + "-∞)";
            }
            if (ttrTo > 0) {
                name += " (0-" + ttrTo + ")";
            }
        }
        if (name.isEmpty()) {
            return "Alle Spieler";
        }
        return name;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public String convertToJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstname", firstname);
        jsonObject.put("lastname", lastname);
        if (club != null) {
            jsonObject.put("clubname", club.getName());
            jsonObject.put("clubid", club.getId());
            jsonObject.put("clubverband", club.getVerband());
        } else {
            jsonObject.put("clubname", "");
            jsonObject.put("clubid", "");
            jsonObject.put("clubverband", "");
        }
        jsonObject.put("ttrFrom", ttrFrom);
        jsonObject.put("ttrTo", ttrTo);
        return jsonObject.toString();
    }

    public static SearchPlayer convertFromJson(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        SearchPlayer sp = new SearchPlayer();
        sp.setFirstname(jsonObject.getString("firstname"));
        sp.setLastname(jsonObject.getString("lastname"));

        if (StringUtils.isNoneEmpty(jsonObject.getString("clubname"),
                jsonObject.getString("clubid"),
                jsonObject.getString("clubverband"))) {

            Club c = new Club(jsonObject.getString("clubname"),
                    jsonObject.getString("clubid"),
                    jsonObject.getString("clubverband"));
            sp.setClub(c);
        }
        sp.setTtrFrom(jsonObject.getInt("ttrFrom"));
        sp.setTtrTo(jsonObject.getInt("ttrTo"));
        return sp;
    }

    private String add(String str, String name) {
        if (str.isEmpty())
            return name;
        else
            return str + "-" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SearchPlayer that = (SearchPlayer) o;

        return new EqualsBuilder()
                .append(yearFrom, that.yearFrom)
                .append(yearTo, that.yearTo)
                .append(ttrFrom, that.ttrFrom)
                .append(ttrTo, that.ttrTo)
                .append(lastname, that.lastname)
                .append(firstname, that.firstname)
                .append(club.getName(), that.club.getName())
                .append(gender, that.gender)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(lastname)
                .append(firstname)
                .append(club)
                .append(gender)
                .append(yearFrom)
                .append(yearTo)
                .append(ttrFrom)
                .append(ttrTo)
                .toHashCode();
    }
}
