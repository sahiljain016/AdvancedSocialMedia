package com.gic.memorableplaces.DataModels;

import java.io.Serializable;
import java.util.ArrayList;

public class FFUserDetails implements Serializable {
    private String TargetDisplayName;
    private String MyName;
    private String Desp;
    private String TargetUID;
    private String TargetGender;
    private String TargetUsername;
    private String MyUsername;
    private String MyProfilePic;
    private String FiltersMatched;
    private ArrayList<String> alsImagesList;

    private ArrayList<String> alsTargetFilterList;
    private ArrayList<String> alsMyFilterList;
    private ArrayList<Integer> aliIconList;
    private int MatchPercentage;

    public FFUserDetails() {

    }

    public FFUserDetails(String targetDisplayName, String myName, String desp, String targetUID, String targetUsername, String myUsername,
                         String myProfilePic, String TargetGender, String filtersMatched, ArrayList<String> alsImagesList,
                         ArrayList<String> alsTargetFilterList, ArrayList<String> alsMyFilterList, ArrayList<Integer> aliIconList,
                         int matchPercentage) {
        TargetDisplayName = targetDisplayName;
        MyName = myName;
        Desp = desp;
        TargetUID = targetUID;
        TargetUsername = targetUsername;
        MyUsername = myUsername;
        this.TargetGender = TargetGender;
        MyProfilePic = myProfilePic;
        FiltersMatched = filtersMatched;
        this.alsImagesList = alsImagesList;
        this.alsTargetFilterList = alsTargetFilterList;
        this.alsMyFilterList = alsMyFilterList;
        this.aliIconList = aliIconList;
        MatchPercentage = matchPercentage;
    }

    public String getTargetDisplayName() {
        return TargetDisplayName;
    }

    public void setTargetDisplayName(String targetDisplayName) {
        TargetDisplayName = targetDisplayName;
    }

    public String getMyName() {
        return MyName;
    }

    public void setMyName(String myName) {
        MyName = myName;
    }

    public String getDesp() {
        return Desp;
    }

    public void setDesp(String desp) {
        Desp = desp;
    }

    public String getTargetUID() {
        return TargetUID;
    }

    public void setTargetUID(String targetUID) {
        TargetUID = targetUID;
    }

    public String getTargetUsername() {
        return TargetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        TargetUsername = targetUsername;
    }

    public String getMyUsername() {
        return MyUsername;
    }

    public void setMyUsername(String myUsername) {
        MyUsername = myUsername;
    }

    public String getTargetGender() {
        return TargetGender;
    }

    public void setTargetGender(String targetGender) {
        TargetGender = targetGender;
    }

    public String getMyProfilePic() {
        return MyProfilePic;
    }

    public void setMyProfilePic(String myProfilePic) {
        MyProfilePic = myProfilePic;
    }

    public String getFiltersMatched() {
        return FiltersMatched;
    }

    public void setFiltersMatched(String filtersMatched) {
        FiltersMatched = filtersMatched;
    }

    public ArrayList<String> getAlsImagesList() {
        return alsImagesList;
    }

    public void setAlsImagesList(ArrayList<String> alsImagesList) {
        this.alsImagesList = alsImagesList;
    }

    public ArrayList<String> getAlsTargetFilterList() {
        return alsTargetFilterList;
    }

    public void setAlsTargetFilterList(ArrayList<String> alsTargetFilterList) {
        this.alsTargetFilterList = alsTargetFilterList;
    }

    public ArrayList<String> getAlsMyFilterList() {
        return alsMyFilterList;
    }

    public void setAlsMyFilterList(ArrayList<String> alsMyFilterList) {
        this.alsMyFilterList = alsMyFilterList;
    }

    public ArrayList<Integer> getAliIconList() {
        return aliIconList;
    }

    public void setAliIconList(ArrayList<Integer> aliIconList) {
        this.aliIconList = aliIconList;
    }

    public int getMatchPercentage() {
        return MatchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        MatchPercentage = matchPercentage;
    }


    @Override
    public String toString() {
        return "FFUserDetails{" +
                "FullName='" + TargetDisplayName + '\'' +
                ", MyName='" + MyName + '\'' +
                ", Desp='" + Desp + '\'' +
                ", TargetUID='" + TargetUID + '\'' +
                ", TargetUsername='" + TargetUsername + '\'' +
                ", MyUsername='" + MyUsername + '\'' +
                ", MyProfilePic='" + MyProfilePic + '\'' +
                ", FiltersMatched='" + FiltersMatched + '\'' +
                ", alsImagesList=" + alsImagesList +
                ", alsTargetFilterList=" + alsTargetFilterList +
                ", alsMyFilterList=" + alsMyFilterList +
                ", aliIconList=" + aliIconList +
                ", MatchPercentage=" + MatchPercentage +
                '}';
    }
}
