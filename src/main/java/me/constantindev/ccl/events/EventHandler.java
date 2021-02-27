package me.constantindev.ccl.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.constantindev.ccl.events.EventPriority;

/**
 * Used to mark listeners.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    int priority() default EventPriority.MEDIUM;
}