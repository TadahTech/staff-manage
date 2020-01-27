package com.tadahtech.mc.staffmanage.database;

import com.tadahtech.mc.staffmanage.database.ColumnType.ColumnAttribute;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SavedFieldMeta<T> {

    private Field field;
    private Saved objectField;

    public SavedFieldMeta(Field field, Saved objectField) {
        this.field = field;
        this.objectField = objectField;
    }

    public Object get(T object) {
        try {
            this.field.setAccessible(true);
            return this.field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SavedFieldValue<T> getValue(T object) {
        Object value = this.get(object);
        if (value == null && !this.canBeNull()) {
            throw new NullPointerException("Field tagged as not-null \"" + this.field.getName() + "\" is null in given object!");
        }

        return new SavedFieldValue<>(this, value);
    }

    public void set(T object, Object value) {
        try {
            this.field.setAccessible(true);
            this.field.set(object, value == null ? null : this.getColumnType().getHandler().deserialize(this, value));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isUnique() {
        return this.objectField.unique();
    }

    public Field getField() {
        return field;
    }

    public ColumnType getColumnType() {
        return this.objectField.columnType();
    }

    public String getName() {
        return this.objectField.name().isEmpty() ? this.field.getName() : this.objectField.name();
    }

    public String getExactName() {
        return "`" + this.getName() + "`";
    }

    public boolean canBeNull() {
        return !this.hasAttribute(ColumnAttribute.NOT_NULL) && !this.isPrimaryKey();
    }

    public boolean isPrimaryKey() {
        return this.objectField.primaryKey();
    }

    public int getSize() {
        return this.objectField.size();
    }

    public String toSQLFieldDeclaration() {
        return this.getExactName() + " " + this.getColumnType().toSQL(this.getSize()) + (this.isUnique() ? " UNIQUE" : "");
    }

    public ColumnAttribute[] getAttributes() {
        return this.objectField.attributes();
    }

    public boolean hasAttribute(ColumnAttribute attribute) {
        return Arrays.stream(this.getAttributes()).anyMatch(attribute1 -> attribute1 == attribute);
    }

    public Class<?> getTypeParameter(int index) {
        return this.objectField.typeParameters()[index];
    }

    public boolean isExclude() {
        return this.objectField.exclude();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SavedFieldMeta)) {
            return false;
        }

        SavedFieldMeta<?> that = (SavedFieldMeta<?>) o;

        return new EqualsBuilder()
          .append(field, that.field)
          .append(objectField, that.objectField)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
          .append(field)
          .append(objectField)
          .toHashCode();
    }

    @Override
    public String toString() {
        return "SavedFieldMeta{" +
          "field=" + field +
          ", objectField=" + objectField +
          '}';
    }
}
