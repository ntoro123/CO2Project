package com.example.myapplication;

public class User {

    private String name;
    private String city;
    private double driveaverage;
    private String gasdataSet;
    private double gasaverage;
    private String caryear;
    private String elecdataSet;
    private String drivedataSet;
    private double elecaverage;
    private boolean isCurrentUser;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String city, double driveaverage, String gasdataSet, double gasaverage,
                String caryear, String elecdataSet, String drivedataSet, double elecaverage) {
        this.name = name;
        this.city = city;
        this.driveaverage = driveaverage;
        this.gasdataSet = gasdataSet;
        this.gasaverage = gasaverage;
        this.caryear = caryear;
        this.elecdataSet = elecdataSet;
        this.drivedataSet = drivedataSet;
        this.elecaverage = elecaverage;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public double getDriveaverage() {
        return driveaverage;
    }

    public String getGasdataSet() {
        return gasdataSet;
    }

    public double getGasaverage() {
        return gasaverage;
    }

    public String getCaryear() {
        return caryear;
    }

    public String getElecdataSet() {
        return elecdataSet;
    }

    public String getDrivedataSet() {
        return drivedataSet;
    }

    public double getElecaverage() {
        return elecaverage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDriveaverage(double driveaverage) {
        this.driveaverage = driveaverage;
    }

    public void setGasdataSet(String gasdataSet) {
        this.gasdataSet = gasdataSet;
    }

    public void setGasaverage(double gasaverage) {
        this.gasaverage = gasaverage;
    }

    public void setCaryear(String caryear) {
        this.caryear = caryear;
    }

    public void setElecdataSet(String elecdataSet) {
        this.elecdataSet = elecdataSet;
    }

    public void setDrivedataSet(String drivedataSet) {
        this.drivedataSet = drivedataSet;
    }

    public void setElecaverage(double elecaverage) {
        this.elecaverage = elecaverage;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }
}
