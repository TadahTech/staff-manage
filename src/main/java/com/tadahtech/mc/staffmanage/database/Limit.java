package com.tadahtech.mc.staffmanage.database;

public class Limit implements AdditionalSpecification {

    private int amount;
    private int offset;

    public Limit(int amount) {
        this(amount, 0);
    }

    public Limit(int amount, int offset) {
        this.offset = offset;
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be above 0!");
        }

        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toSQL() {
        return "LIMIT " + this.getAmount() + (this.getOffset() <= 0 ? "" : " OFFSET " + this.getOffset());
    }
}
