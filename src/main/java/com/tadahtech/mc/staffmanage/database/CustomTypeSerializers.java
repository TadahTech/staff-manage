package com.tadahtech.mc.staffmanage.database;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.database.ColumnType.DataSerializer;

import java.util.Map;

public class CustomTypeSerializers {
    private static final Map<Class<?>, DataSerializer> DATA_SERIALIZER_BY_CLASS = Maps.newHashMap();

    public static void register(Class<?> type, DataSerializer serializer) {
        DATA_SERIALIZER_BY_CLASS.put(type, serializer);
    }

    public static DataSerializer getSerializer(Class<?> type) {
        return DATA_SERIALIZER_BY_CLASS.get(type);
    }
}
