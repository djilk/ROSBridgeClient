/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jilk.ros.message;

import java.lang.annotation.*;

/**
 *
 * @author david_000
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageType {
    String string() default "";
}

