package com.tadahtech.mc.staffmanage.database;

/**
 * An additional clause/specification to be appended at the end
 * of a statement.
 */
public interface AdditionalSpecification {

    /**
     * @return The string to be appended
     */
    String toSQL();
}
