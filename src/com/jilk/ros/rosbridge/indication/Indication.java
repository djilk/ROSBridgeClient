/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.indication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author David J. Jilk
 */
public class Indication {
    public static boolean isIndicated(Field f) {
        return (f.getAnnotation(Indicated.class) != null);
    }
    
    public static boolean asArray(Field f) {
        return (f.getAnnotation(AsArray.class) != null);
    }
    
    public static String getIndicatorName(Class c) {
        return getName(c, Indicator.class);
    }
    
    public static String getIndicatedName(Class c) {
        return getName(c, Indicated.class);
    }
    
    private static String getName(Class c, Class annotation) {
        String result = null;
        for (Field f : c.getFields()) {
            if (f.getAnnotation(annotation) != null) {
                result = f.getName();
                break;
            }
        }
        return result;
    }

    /*
    public static Class getIndication(Object o, String s) {
        Class c = o.getClass();
        Class result = null;
        try {
            Method m = getIndicateMethod(c);
            result = (Class) (m.invoke(o, s));
        }
        catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    private static Method getIndicateMethod(Class c) {
        Method result = null;
        for (Method m : c.getMethods()) {
            if (m.getAnnotation(Indicate.class) != null) {
                result = m;
                break;
            }
        }
        return result;
    }
    */
}
