package com.diginori.ramdori;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserHistory extends RealmObject {
    @PrimaryKey
    private String          yyyymmdd;

    // Standard getters & setters generated by your IDE…
    public String getYyyymmdd() { return yyyymmdd; }
    public void   setYyyymmdd(String yyyymmdd) { this.yyyymmdd = yyyymmdd; }
}