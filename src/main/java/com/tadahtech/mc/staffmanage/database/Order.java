package com.tadahtech.mc.staffmanage.database;

public class Order implements AdditionalSpecification {

    private SavedFieldMeta field;
    private Policy policy;

    public Order(SavedFieldMeta field, Policy policy) {
        this.field = field;
        this.policy = policy;
    }

    public Policy getPolicy() {
        return policy;
    }

    public SavedFieldMeta getField() {
        return field;
    }

    @Override
    public String toSQL() {
        return "ORDER BY " + this.getField().getExactName() + " " + this.getPolicy().getSql();
    }

    public enum Policy {
        ASCENDING("ASC"),
        DESCENDING("DESC");

        private String sql;

        Policy(String sql) {
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }
}
