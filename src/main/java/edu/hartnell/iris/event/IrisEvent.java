package edu.hartnell.iris.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IrisEvent {
    Priority priority() default Priority.normal;

    enum Priority {
        high, normal, low
    }
}
