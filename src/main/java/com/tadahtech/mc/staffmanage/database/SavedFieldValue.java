package com.tadahtech.mc.staffmanage.database;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SavedFieldValue<T> {

    private SavedFieldMeta<T> column;
    private Object value;
    private Operator operator;

    public SavedFieldValue(SavedFieldMeta<T> column, Object value) {
        this(column, value, Operator.EQUAL_TO);
    }

    public SavedFieldValue(SavedFieldMeta<T> column, Object value, Operator operator) {
        this.column = column;
        this.value = value;
        this.operator = operator;
    }

    public SavedFieldMeta getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        return this.value == null ? "NULL" : this.column.getColumnType().getHandler().serialize(this.column, this.value);
    }

    public String toSQL() {
        return this.getColumn().getExactName() + this.operator.getSql() + this.getStringValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SavedFieldValue)) {
            return false;
        }

        SavedFieldValue<?> that = (SavedFieldValue<?>) o;

        return new EqualsBuilder()
          .append(column, that.column)
          .append(value, that.value)
          .append(operator, that.operator)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
          .append(column)
          .append(value)
          .append(operator)
          .toHashCode();
    }

    public enum Operator {
        EQUAL_TO("="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL_TO("<="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL_TO(">=");

        private String sql;

        Operator(String sql) {
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }
}
