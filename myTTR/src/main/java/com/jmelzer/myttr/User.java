/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

/*
* Author: J. Melzer
* Date: 31.12.13 
*
*/


package com.jmelzer.myttr;

import java.util.Date;

public class User {
    String username;
    String password;
    String realName;
    String clubName;
    int points;
    Date changedAt;

    public User(String realName, String username, String password, int points, Date changedAt, String clubName) {
        this.realName = realName;
        this.username = username;
        this.password = password;
        this.points = points;
        this.changedAt = changedAt;
        this.clubName = clubName;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }

    public String getRealName() {
        return realName;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public String getClubName() {
        return clubName;
    }
}
