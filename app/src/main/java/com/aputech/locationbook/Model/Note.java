package com.aputech.locationbook.Model;


import java.util.Map;

public class Note {
    private String AlarmTime,Desc,GeoCode,LastEdited,LatLng,Title,audio,color,image,video;
    private int type;
    private boolean pinned;


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlarmTime() {
        return AlarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        AlarmTime = alarmTime;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getGeoCode() {
        return GeoCode;
    }

    public void setGeoCode(String geoCode) {
        GeoCode = geoCode;
    }

    public String getLastEdited() {
        return LastEdited;
    }

    public void setLastEdited(String lastEdited) {
        LastEdited = lastEdited;
    }

    public String getLatLng() {
        return LatLng;
    }

    public void setLatLng(String latLng) {
        LatLng = latLng;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
