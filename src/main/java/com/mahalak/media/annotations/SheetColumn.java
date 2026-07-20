package com.mahalak.media.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SheetColumn {

    String name();

    boolean id() default false;

    String prefix() default "";

    boolean ignore() default false;

    int order() default Integer.MAX_VALUE;
}