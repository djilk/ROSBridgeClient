/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.implementation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author David J. Jilk
 */
public class Registry<T> extends HashMap<Class, Map<String, T>> {
    
    public void register(Class c, String s, T t) {
        Map<String, T> table = get(c);
        if (table == null) {
            table = new HashMap<String, T>();
            put(c, table);
        }
        table.put(s, t);
    }
    
    public void unregister(Class c, String s) {
        Map<String, T> table = get(c);
        if (table != null)
            table.remove(s);
    }
    
    public T lookup(Class c, String s) {
        T result = null;
        Map<String, T> table = get(c);
        if (table != null)
            result = table.get(s);
        return result;
    }
}
