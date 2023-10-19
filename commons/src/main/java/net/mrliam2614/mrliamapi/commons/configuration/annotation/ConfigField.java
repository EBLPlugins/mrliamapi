package net.mrliam2614.mrliamapi.commons.configuration.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface ConfigField {
    String path();
}
