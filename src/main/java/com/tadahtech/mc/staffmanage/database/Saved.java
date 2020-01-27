package com.tadahtech.mc.staffmanage.database;

import com.tadahtech.mc.staffmanage.database.ColumnType.ColumnAttribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Saved {

    ColumnType columnType() default ColumnType.STRING;

    String name() default "";

    boolean primaryKey() default false;

    boolean exclude() default false;

    int size() default 16;

    ColumnAttribute[] attributes() default {};

    boolean unique() default false;

    Class<?>[] typeParameters() default {};
}
