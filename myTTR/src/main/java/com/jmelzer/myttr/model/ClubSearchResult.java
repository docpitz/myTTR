package com.jmelzer.myttr.model;

import androidx.annotation.NonNull;

public class ClubSearchResult implements Comparable<ClubSearchResult>{
    String name;
    float score;

    public ClubSearchResult(String name, float score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(@NonNull ClubSearchResult o) {
        return Float.compare(score, o.score);
    }

    public String getName() {
        return name;
    }

    public float getScore() {
        return score;
    }
}
