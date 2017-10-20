package com.example.android.quakereport;

/**
 * Created by sayak on 19-09-2017.
 */

public class Earthquake {
    private double mMagnitude;
    private String mlocation;
    private long mTimeInMilliseconds;
    private String mUrl;

    public Earthquake(double mag,String loc,long timeInMilliseconds,String url)
    {
        mMagnitude=mag;
        mlocation=loc;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl=url;
    }
    public double getmMagnitude(){return mMagnitude;}
    public String getmlocation(){return mlocation;}
    public long getmDate(){return mTimeInMilliseconds;}
    public String getUrl() {return mUrl;}
}
/*
public Earthquake(String magnitude, String location, long timeInMilliseconds) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
  }
* */