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

public class User {
    String username;
    String password;
    int points;

    public User(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
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

    public void setPoints(int points) {
        this.points = points;
    }
}
