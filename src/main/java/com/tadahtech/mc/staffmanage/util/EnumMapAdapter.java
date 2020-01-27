package com.tadahtech.mc.staffmanage.util;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

public class EnumMapAdapter implements JsonDeserializer<EnumMap<? extends Enum, ?>>, JsonSerializer<EnumMap<? extends Enum, ?>> {

    @Override
    public EnumMap<? extends Enum, ?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonObject data = object.get("data").getAsJsonObject();

        try {
            Class<? extends Enum> keyClass = (Class<? extends Enum>) Class.forName(object.get("keyClass").getAsString());
            Class<?> valueClass = Class.forName(object.get("valueClass").getAsString());

            EnumMap map = Maps.newEnumMap(keyClass);

            Enum[] constants = keyClass.getEnumConstants();

            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                int ordinal = Integer.valueOf(entry.getKey());
                Object value = Common.GSON.fromJson(entry.getValue(), valueClass);
                map.put(constants[ordinal], value);
            }

            return map;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public JsonElement serialize(EnumMap<? extends Enum, ?> enumMap, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        JsonObject data = new JsonObject();

        for (Map.Entry<? extends Enum, ?> entry : enumMap.entrySet()) {
            Enum key = entry.getKey();
            Object value = entry.getValue();

            if (object.entrySet().size() == 0) {
                object.addProperty("keyClass", key.getClass().getName());
                object.addProperty("valueClass", value.getClass().getName());
            }

            data.add(String.valueOf(key.ordinal()), Common.GSON.toJsonTree(value));
        }

        object.add("data", data);

        return object;
    }
}
