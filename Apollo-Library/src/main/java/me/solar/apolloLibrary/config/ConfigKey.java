package me.solar.apolloLibrary.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Annotation to specify the config key and optional comment for a field or type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConfigKey {

    /**
     * The config key name.
     * @return the key name
     */
    String value();

    /**
     * Optional comments for the config key.
     * @return array of comments
     */
    String[] comment() default {""};

}
