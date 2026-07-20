package com.mahalak.media.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheet {

    /**
     * Google Sheet Tab Name
     * Example : Products, Blogs, Users
     */
    String name();

}
