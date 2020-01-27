package com.tadahtech.mc.staffmanage.database;

import java.sql.SQLException;

/**
 * This class is to be used to handling SQL based callbacks, typically those involving ResultSets
 */
public interface SQLCallback<T> {

    void call(T t) throws SQLException;

}
