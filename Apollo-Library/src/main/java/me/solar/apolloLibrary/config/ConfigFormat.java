package me.solar.apolloLibrary.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the format type of a config class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigFormat {

    /**
     * The format type of the config (e.g., YAML, JSON).
     * @return the format type
     */
    FormatType value();

}
