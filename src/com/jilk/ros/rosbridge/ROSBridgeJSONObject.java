/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.Writer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList; 

import java.lang.reflect.Field;


/**
 *
 * @author David J. Jilk
 */
public class ROSBridgeJSONObject extends JSONObject {
    ROSBridgeJSONObject() {
    }
        
    // This seems to work for one-dimensional arrays; haven't tried multi-dimensional
    public void set(Object o) {
        for (Field f : o.getClass().getFields()) {
            Class c = f.getType();
            
            // if getFieldObject(f, o) is null, don't add it to the hash
            //System.out.println(f.getName() + ": " + c.getName());
            if (c.isPrimitive() || c.equals(String.class)) {
                    put(f.getName(), getFieldObject(f, o));
            }
            else if (c.isArray()) {
                List<Object> list = new ArrayList();
                Object arrayObject = getFieldObject(f, o);
                Class arrayClass = c.getComponentType();
                for (int i = 0; i < Array.getLength(arrayObject); i++) {
                    Object element;
                    if (arrayClass.isPrimitive() || arrayClass.equals(String.class))
                        element = Array.get(arrayObject, i);
                    else if (arrayClass.isArray()) {
                        element = null;
                    }
                    else {
                        ROSBridgeJSONObject rbjo = new ROSBridgeJSONObject();
                        rbjo.set(Array.get(arrayObject, i));
                        element = rbjo;
                    }
                    list.add(element);
                }
                put(f.getName(), list);
            }
            else {
                ROSBridgeJSONObject rbjo = new ROSBridgeJSONObject();
                rbjo.set(getFieldObject(f, o));
                put(f.getName(), rbjo);
            }
        }
    }
    

/* 
 * To handle multi-dimensional arrays and make this more elegant, 
 * we need to do something like the following, but they we can't have this
 * be a subclass of JSONObject, we just need to return a JSONObject.
 
    
    public void set2(Object o) {
        if (!o.getClass().isPrimitive() && 
                !o.getClass().isArray() &&
                !o.getClass().equals(String.class))
            set3(o);
    }
    
    private void set3(Object o) {
        Class c = o.getClass();
        if (c.isPrimitive() || c.equals(String.class)) {
            
        }
        else if (c.isArray()) {
            
        }
        else {
            
        }
    }
*/
    
    public static JSONObject get(String data) {
        JSONObject result = null;
        StringReader r = new StringReader(data);
        JSONParser jp = new JSONParser();
        try {
            result = (JSONObject) jp.parse(r);
        }
        catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        r.close();        
        return result;
    }
    
    public static ROSBridgeOperation operationFactory(String data) {
        JSONObject j = get(data);
        ROSBridgeOperation result = null;
        
        String operation = (String) j.get("op");
        Class c = ROSBridgeOperation.class;
        if ("publish".equals(operation))
            c = PublishOperation.class;
        else if ("subscribe".equals(operation))
            c = SubscribeOperation.class;
        
        // If we're still good, get the constructor (trying both Block and List)
        Constructor construct;
        Class[] parameters = new Class[] {JSONObject.class};
        try {
            construct = c.getConstructor(parameters);
            result = (ROSBridgeOperation) construct.newInstance(j);
        }
        catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

        return result;
    }
    
    private Object getFieldObject(Field f, Object o) {
        Object fo = null;
        try {
            fo = f.get(o);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return fo;
    }
    
    public static void main(String[] args) {
        TestClass t = new TestClass();
        t.myLong = 100000000;
        t.myDouble = 3.14159;
        t.myString = "Test String";
        t.myTS = new TestSubClass();
        t.myTS.mySubString = "Test Sub String";
        t.myTS.myBooleanArray = new boolean[] {false, true};
        ROSBridgeJSONObject rbjo = new ROSBridgeJSONObject();
        rbjo.set(t);
        System.out.println(rbjo.toJSONString());
    }
    
    private static class TestClass {
        public long myLong;
        public double myDouble;
        public String myString;
        public TestSubClass myTS;
    }
    
    private static class TestSubClass {
        public String mySubString;
        public boolean[] myBooleanArray;
    }
        
}
