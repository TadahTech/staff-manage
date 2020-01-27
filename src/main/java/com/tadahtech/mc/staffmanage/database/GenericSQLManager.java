package com.tadahtech.mc.staffmanage.database;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.StaffManage;
import com.tadahtech.mc.staffmanage.database.ColumnType.ColumnAttribute;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores generic objects implementing the {@link Savable} interface.
 *
 * @param <T> Type to be stored by this instance. Must have a public no-args
 *            constructor, unless you wish to ONLY use the {@link #update(T)}
 *            method for fetching from the SQL database. All fields that you
 *            wish to store must be annotated using the {@link Saved}
 *            annotation.
 * @author Eirik Lorgen Tanberg
 */
public class GenericSQLManager<T extends Savable> extends SQLManager {

    private String tableName;
    private Class<T> objectClass;
    private SavedFieldMeta<T>[] primaries;
    private SavedFieldMeta<T>[] fields;

    public GenericSQLManager(String tableName, Class<T> objectClass) {
        this(tableName, objectClass, true);
    }

    /**
     * Constructor. On call, this will iterate through the declared fields in
     * the given class. It will look for the {@link Saved} annotation, and
     * save that data in 2 arrays (All and primaries). A table to store in is
     * also created, but on an async thread. Due to this, you have to wait
     * a bit after construction before you can interact (Save, get or delete)
     * anything stored in given table. You can avoid this wait by specifying
     * that you do not wish to create the table now, and then manually call
     * {@link #runCreateTableCommand()} on current thread later.
     *
     * @param tableName   The name of the MySQL table the data is to be stored
     *                    in
     * @param objectClass The class of the object that is stored in THIS instance.
     *                    Must have a public no-args constructor, unless you
     *                    wish to ONLY use the {@link #update(T)} method
     *                    for fetching from the SQL database. All fields that
     *                    you wish to store must be annotated using the
     *                    {@link Saved} annotation.
     * @param createTable Whether or not to create the table immediately on
     *                    async thread. Alternatively you would need to call
     *                    {@link #runCreateTableCommand()} at a later point
     *                    in time.
     */
    public GenericSQLManager(String tableName, Class<T> objectClass, boolean createTable) {
        super(false);
        this.tableName = tableName;
        this.objectClass = objectClass;

        List<SavedFieldMeta<T>> fields = new ArrayList<>();
        List<SavedFieldMeta<T>> primaries = new ArrayList<>();
        for (Field field : objectClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Saved.class)) {
                continue;
            }

            Saved objectField = field.getAnnotation(Saved.class);
            SavedFieldMeta<T> data = new SavedFieldMeta<>(field, objectField);
            fields.add(data);
            if (!objectField.primaryKey()) {
                continue;
            }

            primaries.add(data);
        }

        this.fields = fields.toArray(new SavedFieldMeta[fields.size()]);
        this.primaries = primaries.toArray(new SavedFieldMeta[primaries.size()]);
        if (!createTable) {
            return;
        }

        runAsync(super::runCreateTableCommand);
    }

    /**
     * @return The name of the table this instance saves to
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return The class of the object this instance saves
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * @return Primary key object fields of this instance's object class
     */
    public SavedFieldMeta<T>[] getPrimaries() {
        return primaries;
    }

    /**
     * @return Object fields of this instance's object class
     */
    public SavedFieldMeta<T>[] getFields() {
        return fields;
    }

    /**
     * Gets a field with the exact same name as the one given
     *
     * @param name The exact name (Case sensitive)
     * @return The field, null if not found
     */
    public SavedFieldMeta<T> getField(String name) {
        return Arrays.stream(this.getFields()).filter(field -> field.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Creates an instance of {@link SavedFieldValue} with the given value.
     * This can be used when getting from the database.
     *
     * @param field The name of the field
     * @param value The value
     * @return A new {@link SavedFieldValue} instance
     */
    public SavedFieldValue<T> createValue(String field, Object value) {
        return new SavedFieldValue<>(this.getField(field), value);
    }

    /**
     * Creates an instance of {@link SavedFieldValue} with the given value.
     * This can be used when getting from the database.
     *
     * @param field    The name of the field
     * @param value    The value
     * @param operator The {@link SavedFieldValue.Operator operator} to compare by
     * @return A new {@link SavedFieldValue} instance
     */
    public SavedFieldValue<T> createValue(String field, Object value, SavedFieldValue.Operator operator) {
        return new SavedFieldValue<>(this.getField(field), value, operator);
    }

    @Override
    public String createTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.tableName + "(");
        for (SavedFieldMeta<T> data : this.fields) {
            builder.append(data.toSQLFieldDeclaration());
            for (ColumnAttribute attribute : data.getAttributes()) {
                builder.append(" ").append(attribute.toSQL());
            }

            builder.append(", "); // We are adding primary keys anyway
        }

        builder.append("PRIMARY KEY(");
        for (int i = 0; i < this.primaries.length; i++) {
            SavedFieldMeta data = this.primaries[i];
            builder.append(data.getExactName());
            if ((i + 1) >= this.primaries.length) {
                continue;
            }

            builder.append(", ");
        }

        return builder.append("));").toString();
    }

    /**
     * Synchronous utility method for getting the next AUTO_INCREMENT value, if such a value
     * is present for this table.
     *
     * @return The next auto increment column value
     */
    public int getNextAutoIncrement() {
        try (Connection connection = this.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES " +
              "WHERE TABLE_SCHEMA='" + StaffManage.getInstance().getSqlConfig().getDatabase() + "' AND TABLE_NAME='" + this.tableName + "';");

            ResultSet resultSet = statement.executeQuery();
            return (resultSet == null || !resultSet.next()) ? 1 : (resultSet.getInt(1) + 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Saves the given object instance's data to the database. If a row with the same
     * primary values as the given object, only the provided fields will be updated.
     * This method interacts with the database on an async thread.
     * <p>
     * Alias for {@link #saveSpecific(Savable, SavedFieldMeta[])}, where you don't need
     * to give the full {@link SavedFieldMeta} instances, just the field names. This is
     * simply for ease of use.
     *
     * @param object     The T instance we wish to save
     * @param fieldNames The names of the fields to be updated
     */
    public void saveSpecific(T object, String... fieldNames) {
        SavedFieldMeta<T>[] fields = new SavedFieldMeta[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            SavedFieldMeta<T> meta = this.getField(fieldNames[i]);
            if (meta == null) {
                throw new IllegalArgumentException("Unknown field \"" + fieldNames[i] + "\" for class " + this.objectClass.getName() + "!");
            }

            fields[i] = meta;
        }

        this.saveSpecific(object, fields);
    }

    /**
     * Saves the given object instance's to the database, excluding the given fields.
     *
     * @param object     The T instance we wish to save
     * @param fieldNames The fields to exclude (Not update, these will still be inserted)
     */
    public void saveExcludeSpecific(T object, String... fieldNames) {
        List<String> excluded = Lists.newArrayList(fieldNames);
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        List<SavedFieldMeta<T>> fields = Arrays.stream(this.fields)
          .filter(field -> excluded.contains(field.getName()))
          .collect(Collectors.toList());

        runAsync(() -> this.saveInternal(values, fields, false));
    }

    /**
     * Saves the given object instance's to the database, excluding the given fields.
     *
     * @param object     The T instance we wish to save
     * @param fieldNames The fields to exclude. These will <b>NOT</b> be
     */
    public void saveExcludeSpecificSync(T object, String... fieldNames) {
        List<String> excluded = Lists.newArrayList(fieldNames);
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        List<SavedFieldMeta<T>> fields = Arrays.stream(this.fields)
          .filter(field -> excluded.contains(field.getName()))
          .collect(Collectors.toList());

        this.saveInternal(values, fields, false);
    }

    /**
     * Saves the given object instance's data to the database. If a row with the same
     * primary values as the given object, only the provided fields will be updated.
     * This method interacts with the database on an async thread.
     *
     * @param object The T instance we wish to save
     * @param fields The fields we allow to be updated
     */
    @SafeVarargs
    public final void saveSpecific(T object, SavedFieldMeta<T>... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("Missing fields!");
        }

        object.onSave();
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        List<SavedFieldMeta<T>> metaList = Lists.newArrayList(fields);
        runAsync(() -> this.saveInternal(values, metaList));
    }

    /**
     * Saves the given object instance to the MySQL database, on an async thread.
     *
     * @param object The T instance
     */
    public void save(T object) {
        object.onSave();
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        runAsync(() -> this.saveInternal(values, null));
    }

    /**
     * Saves the given object instance's data to the database. If a row with the same
     * primary values as the given object, only the provided fields will be updated.
     * This method executes on the current thread.
     * <p>
     * Alias for {@link #saveSyncSpecific(Savable, SavedFieldMeta[])}, where you don't need
     * to give the full {@link SavedFieldMeta} instances, just the field names. This is
     * simply for ease of use.
     *
     * @param object     The T instance we wish to save
     * @param fieldNames The names of the fields to be updated
     */
    public void saveSyncSpecific(T object, String... fieldNames) {
        SavedFieldMeta<T>[] fields = new SavedFieldMeta[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            SavedFieldMeta<T> meta = this.getField(fieldNames[i]);
            if (meta == null) {
                throw new IllegalArgumentException("Unknown field \"" + fieldNames[i] + "\" for class " + this.objectClass.getName() + "!");
            }

            fields[i] = meta;
        }

        this.saveSyncSpecific(object, fields);
    }

    /**
     * Saves the given object instance's data to the database. If a row with the same
     * primary values as the given object, only the provided fields will be updated.
     * This method executes on the current thread.
     *
     * @param object The T instance we wish to save
     * @param fields The fields we allow to be updated
     */
    @SafeVarargs
    public final void saveSyncSpecific(T object, SavedFieldMeta<T>... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("Missing fields!");
        }

        object.onSave();
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        List<SavedFieldMeta<T>> metaList = Lists.newArrayList(fields);
        this.saveInternal(values, metaList);
    }

    /**
     * Saves the given object instance to the MySQL database, on the current
     * thread.
     *
     * @param object The T instance
     */
    public void saveSync(T object) {
        object.onSave();
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        this.saveInternal(values, null);
    }

    /**
     * Computes the statement required to save this object into the database
     *
     * @param object The The object (Instance of type parameter)
     * @return The save (INSERT) statement
     */
    public String getSaveStatment(T object) {
        object.onSave();
        SavedFieldValue<T>[] values = new SavedFieldValue[this.fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.fields[i].getValue(object);
        }

        return this.getSaveStatement(values, null, true);
    }

    /**
     * Takes the instance of type that is given, and injects the values found in the
     * database. The values are fetched based on the values of the primary key fields
     * that are declared in the given object. The primary fields are NOT modified.
     * <p>
     * The method basically updates the given object with the values found in the
     * database.
     *
     * @param object The object (Instance of type parameter)
     */
    public void update(T object) {
        SavedFieldValue<T>[] values = this.extractPrimaries(object);
        SQLStatement statement = this.createSelectANDStatement(values);
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            if (!resultSet.next()) {
                return;
            }

            object.preInject();
            for (SavedFieldMeta<T> data : this.fields) {
                if (data.isPrimaryKey()) {
                    continue;
                }

                data.set(object, resultSet.getObject(data.getName()));
            }

            object.postInject();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an instance of T based on the results in the {@link ResultSet}.
     * This method may be considered unsafe compared to using the provided
     * getters in this class.
     * <p>
     * {@link ResultSet#next()} is called by this method. If there is no next,
     * {@code null} is returned.
     *
     * @param resultSet The {@link ResultSet} to create instance based on
     * @return The created instance of T. {@code null} if the result set is
     * empty, or an error occurs.
     */
    public T get(ResultSet resultSet) {
        try {
            if (!resultSet.next()) {
                return null;
            }

            T object = this.objectClass.newInstance();
            object.preInject();
            for (SavedFieldMeta<T> data : this.fields) {
                data.set(object, resultSet.getObject(data.getName()));
            }

            object.postInject();
            return object;
        } catch (InstantiationException | SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates all instances of T possible using the data in the given
     * {@link ResultSet}. This method may be considered unsafe compared
     * to using the provided getters in this class.
     * <p>
     * The method will try creating new instances until there are no
     * more results in the {@link ResultSet}.
     *
     * @param resultSet The {@link ResultSet} to create instances based
     *                  on
     * @return A list of T
     */
    public List<T> getAll(ResultSet resultSet) {
        try {
            List<T> objects = Lists.newArrayList();
            while (resultSet.next()) {
                T object = this.objectClass.newInstance();
                object.preInject();
                for (SavedFieldMeta<T> data : this.fields) {
                    data.set(object, resultSet.getObject(data.getName()));
                }

                object.postInject();
                objects.add(object);
            }

            return objects;
        } catch (IllegalAccessException | InstantiationException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Counts all the rows in the table that matches the given criteria on an
     * async thread
     *
     * @param values   The criteria to match
     * @param callback The callback
     */
    @SafeVarargs
    public final void count(Callback<Integer> callback, SavedFieldValue<T>... values) {
        this.runAsync(() -> {
            int count = this.count(values);
            this.runSync(() -> callback.call(count));
        });
    }

    /**
     * Counts all the rows in the table that matches the given criteria
     *
     * @param values The criteria to match
     * @return The amount of rows that match the given criteria
     */
    @SafeVarargs
    public final int count(SavedFieldValue<T>... values) {
        StringBuilder statementBuilder = new StringBuilder("SELECT COUNT(1) FROM `" + this.tableName + "`");

        if (values.length > 0) {
            statementBuilder.append(" WHERE ");

            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    statementBuilder.append(" AND ");
                }

                statementBuilder.append(values[i].toSQL());
            }
        }

        statementBuilder.append(";");

        SQLStatement statement = new SQLStatement(statementBuilder.toString());
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets an object based on the values given. These values should correspond
     * to the primary keys defined in the type class (Type, order).
     *
     * @param callback      The callback (On sync thread)
     * @param primaryValues The corresponding primary column values
     */
    public void getByPrimaryAsync(Callback<T> callback, Object... primaryValues) {
        runAsync(() -> {
            T object = this.getByPrimary(primaryValues);
            runSync(() -> callback.call(object));
        });
    }

    /**
     * Gets an object based on the values given. These values should correspond
     * to the primary keys defined in the type class (Type, order).
     *
     * @param primaryValues The corresponding primary column values
     * @return The object found in the database, corresponding to the values given.
     * Null if not found. Single return value since these are primary values and
     * thus unique.
     */
    public T getByPrimary(Object... primaryValues) {
        return this.get(this.matchToPrimary(primaryValues));
    }

    /**
     * Gets the first object matching the given values.
     *
     * @param callback The callback (On sync thread)
     * @param values   The values that should match. If none are given, the first
     *                 row in table is returned.
     */
    @SafeVarargs
    public final void getAsync(Callback<T> callback, SavedFieldValue<T>... values) {
        runAsync(() -> {
            T object = this.get(values);
            runSync(() -> callback.call(object));
        });
    }

    /**
     * Gets the first object matching the given values.
     *
     * @param callback       The callback (On sync thread)
     * @param specifications Any additional specifications required
     * @param values         The values that should match. If none are given, the first
     *                       row in table is returned.
     */
    @SafeVarargs
    public final void getAsync(Callback<T> callback, AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        runAsync(() -> {
            T object = this.get(specifications, values);
            runSync(() -> callback.call(object));
        });
    }

    /**
     * Gets the first object matching the given values.
     *
     * @param values The values that should match. If none are given, the first
     *               row in table is returned.
     * @return The first matched object
     */
    @SafeVarargs
    public final T get(SavedFieldValue<T>... values) {
        return this.get(new AdditionalSpecification[0], values);
    }

    /**
     * Gets the first object matching the given values.
     *
     * @param values         The values that should match. If none are given, the first
     *                       row in table is returned.
     * @param specifications Any additional specifications required
     * @return The first matched object
     */
    @SafeVarargs
    public final T get(AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        SQLStatement statement = this.createSelectANDStatement(values, specifications);
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            return this.get(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all objects matching the given values on an async thread.
     *
     * @param callback The callback
     * @param values   The values that should match. If none are given, all objects
     *                 stored are returned.
     */
    @SafeVarargs
    public final void getAllAsync(Callback<List<T>> callback, SavedFieldValue<T>... values) {
        runAsync(() -> {
            List<T> list = this.getAll(values);
            runSync(() -> callback.call(list));
        });
    }

    /**
     * Gets all objects matching the given values on an async thread.
     *
     * @param callback       The callback
     * @param specifications Any additional specifications required
     * @param values         The values that should match. If none are given, all objects
     *                       stored are returned.
     */
    @SafeVarargs
    public final void getAllAsync(Callback<List<T>> callback, AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        runAsync(() -> {
            List<T> list = this.getAll(specifications, values);
            runSync(() -> callback.call(list));
        });
    }

    /**
     * Gets all objects matching the given values.
     *
     * @param values The values that should match. If none are given, all objects
     *               stored are returned.
     * @return All matching objects
     */
    @SafeVarargs
    public final List<T> getAll(SavedFieldValue<T>... values) {
        return this.getAll(new AdditionalSpecification[0], values);
    }

    /**
     * Gets all objects matching the given values.
     *
     * @param values         The values that should match. If none are given, all objects
     *                       stored are returned.
     * @param specifications Any additional specifications required
     * @return All matching objects
     */
    @SafeVarargs
    public final List<T> getAll(AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        SQLStatement statement = this.createSelectANDStatement(values, specifications);
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            return this.getAll(resultSet);
        } catch (Exception e) {
            System.out.println(("Statement: " + statement.toString()));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the first object matching one or more of the given values.
     *
     * @param callback The callback
     * @param values   The values that should match.
     */
    @SafeVarargs
    public final void getFirstMatchAsync(Callback<T> callback, SavedFieldValue<T>... values) {
        runAsync(() -> {
            T object = this.getFirstMatch(values);
            runSync(() -> callback.call(object));
        });
    }

    /**
     * Gets the first object matching one or more of the given values.
     *
     * @param values The values that should match.
     * @return The first matched value
     */
    @SafeVarargs
    public final T getFirstMatch(SavedFieldValue<T>... values) {
        SQLStatement statement = this.createSelectORStatement(values);
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            return this.get(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all objects matching one or more of the given values.
     *
     * @param callback The callback
     * @param values   The values that should match. If none are given, all objects
     *                 stored are returned.
     */
    @SafeVarargs
    public final void getAllMatchesAsync(Callback<List<T>> callback, SavedFieldValue<T>... values) {
        runAsync(() -> {
            List<T> list = this.getAllMatches(values);
            runSync(() -> callback.call(list));
        });
    }

    /**
     * Gets all objects matching one or more of the given values.
     *
     * @param callback       The callback
     * @param specifications Any additional specifications required
     * @param values         The values that should match. If none are given, all objects
     *                       stored are returned.
     */
    @SafeVarargs
    public final void getAllMatchesAsync(Callback<List<T>> callback, AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        runAsync(() -> {
            List<T> list = this.getAllMatches(specifications, values);
            runSync(() -> callback.call(list));
        });
    }

    /**
     * Gets all objects matching one or more of the given values.
     *
     * @param values The values that should match. If none are given, all objects
     *               stored are returned.
     * @return All matching objects
     */
    @SafeVarargs
    public final List<T> getAllMatches(SavedFieldValue<T>... values) {
        return this.getAllMatches(new AdditionalSpecification[0], values);
    }

    /**
     * Gets all objects matching one or more of the given values.
     *
     * @param values         The values that should match. If none are given,
     *                       all objects stored are returned.
     * @param specifications Any additional specifications required
     * @return All matching objects
     */
    @SafeVarargs
    public final List<T> getAllMatches(AdditionalSpecification[] specifications, SavedFieldValue<T>... values) {
        SQLStatement statement = this.createSelectORStatement(values, specifications);
        try (Connection connection = this.getConnection()) {
            ResultSet resultSet = this.getResultSet(statement, connection);
            return this.getAll(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the object, if found in the database on async thread. This utilizes
     * the primary values in the object, which are extracted via reflection. This
     * is done on current thread to avoid concurrency issues.
     *
     * @param object The object to delete
     */
    public void deleteAsync(T object) {
        this.deleteAsync(this.extractPrimaries(object)); // #deleteAsync so that extraction of primaries is sync
    }

    /**
     * Deletes the object, if found in the database. This utilizes the primary values
     * in the object, which are extracted via reflection.
     *
     * @param object The object to delete
     */
    public void deleteSync(T object) {
        this.deleteSync(this.extractPrimaries(object));
    }

    /**
     * Deletes the row corresponding to the given primary values on async thread.
     * These values should correspond to the primary keys defined in the type
     * class (Type, order).
     *
     * @param primaryValues The corresponding primary column values
     */
    public void deleteByPrimaryAsync(Object... primaryValues) {
        runAsync(() -> this.deleteSync(this.matchToPrimary(primaryValues))); // This way instead of #deleteAsync so matching is async too
    }

    /**
     * Deletes the row corresponding to the given primary values. These values
     * should correspond to the primary keys defined in the type class (Type,
     * order).
     *
     * @param primaryValues The corresponding primary column values
     */
    public void deleteByPrimarySync(Object... primaryValues) {
        this.deleteSync(this.matchToPrimary(primaryValues));
    }

    /**
     * Deletes all rows matching the given values on async thread.
     *
     * @param values The values to match
     */
    @SafeVarargs
    public final void deleteAsync(SavedFieldValue<T>... values) {
        runAsync(() -> this.deleteSync(values));
    }

    /**
     * Deletes all rows matching the given values.
     *
     * @param values The values to match
     */
    @SafeVarargs
    public final void deleteSync(SavedFieldValue<T>... values) {
        StringBuilder builder = new StringBuilder("DELETE FROM `" + this.tableName + "`");
        commandAssembly:
        {
            if (values.length == 0) {
                break commandAssembly;
            }

            builder.append(" WHERE ");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(" AND ");
                }

                builder.append(values[i].toSQL());
            }
        }

        builder.append(";");

        SQLStatement statement = new SQLStatement(builder.toString());
        try (Connection connection = this.getConnection()) {
            statement.prepare(connection).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Internal methods

    private SavedFieldValue<T>[] extractPrimaries(T object) {
        SavedFieldValue<T>[] values = new SavedFieldValue[this.primaries.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.primaries[i].getValue(object);
        }

        return values;
    }

    private SavedFieldValue<T>[] matchToPrimary(Object... primaryValues) {
        if (primaryValues.length != this.primaries.length) {
            throw new IllegalArgumentException("Mismatched arguments for " + this.objectClass.getSimpleName() +
              "! Arguments should match with order and count of primaries! Received: " + primaryValues.length +
              ", expected: " + this.primaries.length);
        }

        SavedFieldValue<T>[] values = new SavedFieldValue[primaryValues.length];
        for (int i = 0; i < values.length; i++) {
            SavedFieldMeta<T> data = this.primaries[i];
            Object value = primaryValues[i];
            if (!data.getColumnType().matches(value)) {
                throw new IllegalArgumentException("Mismatched argument type (Index " + i + ")");
            }

            values[i] = new SavedFieldValue<>(data, value);
        }

        return values;
    }

    private SQLStatement createSelectANDStatement(SavedFieldValue<T>[] values, AdditionalSpecification... specifications) {
        StringBuilder builder = new StringBuilder("SELECT * FROM `" + this.tableName + "`");
        commandAssembly:
        {
            if (values.length == 0) {
                break commandAssembly;
            }

            builder.append(" WHERE ");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(" AND ");
                }

                builder.append(values[i].toSQL());
            }
        }

        for (AdditionalSpecification specification : specifications) {
            builder.append(" ").append(specification.toSQL());
        }

        builder.append(";");
        return new SQLStatement(builder.toString());
    }

    private SQLStatement createSelectORStatement(SavedFieldValue<T>[] values, AdditionalSpecification... specifications) {
        StringBuilder builder = new StringBuilder("SELECT * FROM `" + this.tableName + "`");
        commandAssembly:
        {
            if (values.length == 0) {
                break commandAssembly;
            }

            builder.append(" WHERE ");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(" OR ");
                }

                builder.append(values[i].toSQL());
            }
        }

        for (AdditionalSpecification specification : specifications) {
            builder.append(" ").append(specification.toSQL());
        }

        builder.append(";");
        return new SQLStatement(builder.toString());
    }

    private void saveInternal(SavedFieldValue<T>[] values, List<SavedFieldMeta<T>> metaList) {
        this.saveInternal(values, metaList, true);
    }

    private void saveInternal(SavedFieldValue<T>[] values, List<SavedFieldMeta<T>> metaList, boolean listInclusive) {
        String statementStr = this.getSaveStatement(values, metaList, listInclusive);
        SQLStatement statement = new SQLStatement(statementStr);

        try (Connection connection = this.getConnection()) {
            statement.prepare(connection).execute();
        } catch (SQLException e) {
            System.out.println("Statement: " + statementStr);
            e.printStackTrace();
        }
    }

    private String getSaveStatement(SavedFieldValue<T>[] values, List<SavedFieldMeta<T>> metaList, boolean listInclusive) {
        StringBuilder builder = new StringBuilder("INSERT INTO `" + this.tableName + "`(");
        StringBuilder valuesBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();

        int toUpdate = metaList == null ? this.fields.length : (listInclusive ? metaList.size() : (this.fields.length - metaList.size()));

        int updateCount = 0;

        for (int i = 0; i < values.length; i++) {
            SavedFieldValue<T> value = values[i];
            if (value.getValue() == null && !value.getColumn().canBeNull()) {
                throw new IllegalArgumentException("Column " + value.getColumn().getName() + " is null!");
            }

            boolean insert = (metaList == null || listInclusive || !metaList.contains(value.getColumn()))
              && !value.getColumn().isExclude();

            if (insert) {
                builder.append(value.getColumn().getExactName());
                valuesBuilder.append(value.getStringValue());
            }

            boolean update = (!value.getColumn().hasAttribute(ColumnAttribute.AUTO_INCREMENT)
              && (metaList == null || metaList.contains(value.getColumn()) && listInclusive || !metaList.contains(value.getColumn()) && !listInclusive))
              && !value.getColumn().isExclude();

            if (update) {
                updateCount++;
                updateBuilder.append(value.toSQL());
            }

            if ((i + 1) >= values.length) {
                continue;
            }

            if (insert) {
                builder.append(", ");
                valuesBuilder.append(", ");
            }

            if (update && (metaList == null || updateCount < toUpdate)) {
                updateBuilder.append(", ");
            }
        }

        builder.append(") VALUES (").append(valuesBuilder).append(")");

        if (!updateBuilder.toString().isEmpty()) {
            builder.append(" ON DUPLICATE KEY UPDATE ").append(updateBuilder);
        }

        builder.append(";");
        return builder.toString();
    }
}