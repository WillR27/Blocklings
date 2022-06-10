package com.willr27.blocklings.interop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to mark a class as a mod proxy.
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface Proxy
{
    String modid();
    String activeClassName() default "";
}
