package org.example.wifiproject;

import java.time.LocalDateTime;

public class BookmarkList {
    private int wifi_id;
    private int ID;
    private String name;
    private String wifi_name;
    private LocalDateTime createDate;

    public int getWifi_id() {
        return wifi_id;
    }

    public void setWifi_id(int wifi_id) {
        this.wifi_id = wifi_id;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWifi_name() {
        return wifi_name;
    }

    public void setWifi_name(String wifi_name) {
        this.wifi_name = wifi_name;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
