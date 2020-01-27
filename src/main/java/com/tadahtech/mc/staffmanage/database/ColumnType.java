package com.tadahtech.mc.staffmanage.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tadahtech.mc.staffmanage.util.Common;
import com.tadahtech.mc.staffmanage.util.UtilText;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public enum ColumnType {

    UUID("CHAR(36)", true, java.util.UUID.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + object.toString() + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return java.util.UUID.fromString((String) object);
        }
    }),
    DOUBLE("DOUBLE", true, Double.class),
    INTEGER("INTEGER", Integer.class),
    LONG("BIGINT", Long.class),
    BOOLEAN("BOOLEAN", true, Boolean.class),
    DATE("DATETIME", true, Date.class, new DataSerializer() {
        private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "'" + FORMAT.format(object) + "'";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return new Date(((Timestamp) object).getTime());
        }
    }),
    STRING("VARCHAR", String.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + ((String) object).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return object;
        }
    }),
    STRING_LIST("LONGTEXT", true, List.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + UtilText.split("&", (List<String>) object) + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return UtilText.parse("&", (String) object);
        }
    }),
    JSON("LONGTEXT", true, JsonElement.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            if (object == null) {
                return null;
            }

            return "\"" + Common.GSON.toJson((JsonElement) object).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            if (object == null || ((String) object).isEmpty()) {
                return null;
            }

            return Common.PARSER.parse((String) object);
        }
    }),
    ENUM("INT", Enum.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            if (object == null) {
                return "-1";
            }

            return String.valueOf(((Enum) object).ordinal());
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            if ((int) object == -1) {
                return null;
            }

            Class<? extends Enum> enumClass = (Class<? extends Enum>) meta.getField().getType();
            return enumClass.getEnumConstants()[(int) object];
        }
    }),
    JSONABLE("LONGTEXT", true, Jsonable.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + Common.GSON.toJson(((Jsonable) object).toJson()).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            JsonElement element = Common.PARSER.parse((String) object);
            Class<? extends Jsonable> fieldClass = (Class<? extends Jsonable>) meta.getField().getType();

            try {
                Constructor<? extends Jsonable> constructor = fieldClass.getConstructor(JsonElement.class);
                return constructor.newInstance(element);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }

            return null;
        }
    }),
    INT_ARRAY("LONGTEXT", true, int[].class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            int[] array = (int[]) object;
            JsonArray json = new JsonArray();
            for (int i : array) {
                json.add(new JsonPrimitive(i));
            }

            return "\"" + Common.GSON.toJson(json).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            JsonArray array = Common.PARSER.parse((String) object).getAsJsonArray();
            int[] ints = new int[array.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = array.get(i).getAsInt();
            }

            return ints;
        }
    }),
    STRING_ARRAY("LONGTEXT", true, String[].class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            String[] array = (String[]) object;
            JsonArray json = new JsonArray();
            for (String str : array) {
                json.add(new JsonPrimitive(str));
            }

            return "\"" + Common.GSON.toJson(json).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            JsonArray array = Common.PARSER.parse((String) object).getAsJsonArray();
            String[] strings = new String[array.size()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = array.get(i).getAsString();
            }

            return strings;
        }
    }),
    INTEGER_LIST("LONGTEXT", true, List.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            List<Integer> integers = (List<Integer>) object;
            JsonArray json = new JsonArray();
            for (int i : integers) {
                json.add(new JsonPrimitive(i));
            }

            return "\"" + Common.GSON.toJson(json).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            JsonArray array = Common.PARSER.parse((String) object).getAsJsonArray();
            List<Integer> integers = Lists.newArrayList();
            for (int i = 0; i < array.size(); i++) {
                integers.add(array.get(i).getAsInt());
            }

            return integers;
        }
    }),
    CLASS("LONGTEXT", true, Class.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + ((Class) object).getName() + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            try {
                return Class.forName((String) object);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }),
    GSON("LONGTEXT", true, Object.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + Common.GSON.toJson(object).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return Common.GSON.fromJson((String) object, meta.getField().getType());
        }
    }),
    CUSTOM("LONGTEXT", true, Object.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return CustomTypeSerializers.getSerializer(meta.getField().getType()).serialize(meta, object);
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return CustomTypeSerializers.getSerializer(meta.getField().getType()).deserialize(meta, object);
        }
    }),
    SAFE_CLASS_ARRAY("LONGTEXT", true, Class[].class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            JsonArray array = new JsonArray();
            Class[] classes = (Class[]) object;
            for (Class aClass : classes) {
                array.add(new JsonPrimitive(aClass.getName()));
            }

            String string = array.toString();
            String base64 = Base64.getEncoder().encodeToString(string.getBytes());
            return "\"" + base64 + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            String base64 = (String) object;
            String decoded = new String(Base64.getDecoder().decode(base64));
            JsonArray array = Common.PARSER.parse(decoded).getAsJsonArray();
            Class[] classes = new Class[array.size()];
            for (int i = 0; i < classes.length; i++) {
                try {
                    classes[i] = Class.forName(array.get(i).getAsString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return classes;
        }
    }),
    COSMETICS_SAFE_ARRAY("LONGTEXT", true, Class[].class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            JsonArray array = new JsonArray();
            Class[] classes = (Class[]) object;
            for (Class aClass : classes) {
                array.add(new JsonPrimitive(aClass.getName().substring("net.crazywars.core.modules.cosmetics.types.".length())));
            }

            String string = array.toString();
            String base64 = Base64.getEncoder().encodeToString(string.getBytes());
            return "\"" + base64 + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            String base64 = (String) object;
            String decoded = new String(Base64.getDecoder().decode(base64));
            JsonArray array = Common.PARSER.parse(decoded).getAsJsonArray();
            Class[] classes = new Class[array.size()];
            for (int i = 0; i < classes.length; i++) {
                try {
                    classes[i] = Class.forName("net.crazywars.core.modules.cosmetics.types." + array.get(i).getAsString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return classes;
        }
    }),
    LONG_STRING("LONGTEXT", true, String.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            return "\"" + ((String) object).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            return object;
        }
    }),
    GSON_SAFE_LIST("LONGTEXT", true, List.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            List<?> list = (List<?>) object;

            JsonArray array = new JsonArray();
            for (Object element : list) {
                array.add(Common.GSON.toJsonTree(element));
            }

            return "\"" + Common.GSON.toJson(array).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            String serialized = (String) object;
            Type type = meta.getTypeParameter(0);

            JsonArray array = Common.PARSER.parse(serialized).getAsJsonArray();
            List<Object> list = Lists.newLinkedList();

            for (int i = 0; i < array.size(); i++) {
                JsonElement element = array.get(i);
                Object deserialized = Common.GSON.fromJson(element, type);
                list.add(deserialized);
            }

            return list;
        }
    }),
    ENUM_MAP("LONGTEXT", true, EnumMap.class, new DataSerializer() {
        @Override
        public String serialize(SavedFieldMeta meta, Object object) {
            EnumMap<?, ?> map = (EnumMap<?, ?>) object;

            JsonObject data = new JsonObject();
            map.forEach((key, value) -> data.add(String.valueOf(key.ordinal()), Common.GSON.toJsonTree(value)));

            return "\"" + Common.GSON.toJson(data).replace("\"", "\\\"") + "\"";
        }

        @Override
        public Object deserialize(SavedFieldMeta meta, Object object) {
            Class<? extends Enum> keyClass = meta.getTypeParameter(0);
            Class<?> valueClass = meta.getTypeParameter(1);

            EnumMap map = Maps.newEnumMap(keyClass);
            Enum[] constants = keyClass.getEnumConstants();

            JsonObject data = Common.PARSER.parse((String) object).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                int ordinal = Integer.valueOf(entry.getKey());
                Object value = Common.GSON.fromJson(entry.getValue(), valueClass);
                map.put(constants[ordinal], value);
            }

            return map;
        }
    });

    private String sql;
    private boolean forcedSize;
    private Class<?> javaClass;
    private DataSerializer handler;

    ColumnType(String sql, Class<?> javaClass) {
        this(sql, false, javaClass);
    }

    ColumnType(String sql, boolean forcedSize, Class<?> javaClass) {
        this(sql, forcedSize, javaClass, new DataSerializer() {
            @Override
            public String serialize(SavedFieldMeta meta, Object object) {
                return String.valueOf(object);
            }

            @Override
            public Object deserialize(SavedFieldMeta meta, Object object) {
                return object;
            }
        });
    }

    ColumnType(String sql, Class<?> javaClass, DataSerializer handler) {
        this(sql, false, javaClass, handler);
    }

    ColumnType(String sql, boolean forcedSize, Class<?> javaClass, DataSerializer handler) {
        this.sql = sql;
        this.forcedSize = forcedSize;
        this.javaClass = javaClass;
        this.handler = handler;
    }

    public DataSerializer getHandler() {
        return handler;
    }

    public boolean matches(Object object) {
        return this.javaClass.isInstance(object);
    }

    public String toSQL(int size) {
        return this.sql + (this.forcedSize ? "" : ("(" + size + ")"));
    }

    public enum ColumnAttribute {
        AUTO_INCREMENT("AUTO_INCREMENT"),
        NOT_NULL("NOT NULL"),;

        private String sql;

        ColumnAttribute(String sql) {
            this.sql = sql;
        }

        public String toSQL() {
            return sql;
        }
    }

    public interface DataSerializer {

        String serialize(SavedFieldMeta meta, Object object);

        Object deserialize(SavedFieldMeta meta, Object object);
    }
}
