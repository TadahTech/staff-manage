package com.tadahtech.mc.staffmanage.database;

import com.google.gson.JsonElement;

/**
 * An object which may be converted to a JSON object for storage purposes.
 * Must also be able to be converted back, and because of this requires a
 * {@code JsonElement} constructor.
 */
public interface Jsonable {
    JsonElement toJson();
}
