package com.tadahtech.mc.staffmanage.database;

/**
 * Marker interface for classes savable using the {@link GenericSQLManager}.
 * Also features overridable methods called on certain events.
 */
public interface Savable {

    /**
     * Called before values are injected into object instance.
     */
    default void preInject() {
    }

    /**
     * Called after values are injected into object instance.
     */
    default void postInject() {
    }

    /**
     * Called when the object is saved.
     */
    default void onSave() {
    }
}
