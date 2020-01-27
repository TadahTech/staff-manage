package com.tadahtech.mc.staffmanage.database;

public interface SQLConfig {
    String getHost();

    String getDatabase();

    String getUser();

    String getPassword();

    int getPort();
}
