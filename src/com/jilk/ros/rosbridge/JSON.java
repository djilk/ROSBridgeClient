/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.jilk.ros.message.Message;


/**
 *
 * @author David J. Jilk
 */
public class JSON {

    public static String toJSON(Message m) {
        JSONObject jo = toJSONObject(m);
        return jo.toJSONString();
    }
    
    public static Message toMessage(String json, Class c) {
        JSONObject jo = toJSONObject(json);
        return toMessage(jo, c);
    }
    
    private static JSONObject toJSONObject(Object o) {
        JSONObject result = new JSONObject();
        for (Field f : o.getClass().getFields()) {
            Class c = f.getType();
            Object fieldObject = getFieldObject(f, o);
            Object resultObject = null;
            if (fieldObject != null) {
                if (c.isPrimitive() || c.equals(String.class))
                    resultObject = fieldObject;
                else if (c.isArray())
                    resultObject = toJSONArray(fieldObject);
                else
                    resultObject = toJSONObject(fieldObject);
                result.put(f.getName(), resultObject);
            }
        }
        return result;
    }
    
    private static JSONArray toJSONArray(Object array) {
        JSONArray result = new JSONArray();
        Class arrayClass = array.getClass().getComponentType();
        for (int i = 0; i < Array.getLength(array); i++) {
            Object elementObject = Array.get(array, i);
            Object resultObject = null;
            if (elementObject != null) {
                if (arrayClass.isPrimitive() || arrayClass.equals(String.class))
                    resultObject = elementObject;
                else if (arrayClass.isArray())  // this is not actually allowed in ROS
                    resultObject = toJSONArray(elementObject);
                else
                    resultObject = toJSONObject(elementObject);
                result.add(resultObject);
            }
        }
        
        return result;
    }
    
    private static JSONObject toJSONObject(String json) {
        JSONObject result = null;
        StringReader r = new StringReader(json);
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
        
    private static Message toMessage(JSONObject jo, Class c) {
        try {
            Message result = (Message) c.newInstance();
            for (Field f : c.getFields()) {
                Object lookup = jo.get(f.getName());
                Object value = null;
                if (lookup != null) {
                    if (lookup.getClass().equals(JSONObject.class))
                        value = toMessage((JSONObject) lookup, f.getType());
                    else if (lookup.getClass().equals(JSONArray.class))
                        value = toArray((JSONArray) lookup, f.getType().getComponentType());
                    else
                        value = lookup;
                    f.set(result, value);
                }
            }
            
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }        
    }
    
    private static Object toArray(JSONArray ja, Class c) {
        Object result = Array.newInstance(c, ja.size());
        for (int i = 0; i < ja.size(); i++) {
            Object lookup = ja.get(i);
            Object value = null;
            if (lookup != null) {
                if (c.equals(JSONObject.class))
                    value = toMessage((JSONObject) lookup, c);
                else if (lookup.getClass().equals(JSONArray.class))  // this is not actually allowed in ROS
                    value = toArray((JSONArray) lookup, c.getComponentType());
                else 
                    value = lookup;
                Array.set(result, i, value);
            }
        }
        
        return result;
    }
    
        
    /*
    public static Operation operationFactory(String data) {
        JSONObject j = get(data);
        Operation result = null;
        
        String operation = (String) j.get("op");
        Class c = Operation.class;
        if ("publish".equals(operation))
            c = Publish.class;
        else if ("subscribe".equals(operation))
            c = Subscribe.class;
        
        // If we're still good, get the constructor (trying both Block and List)
        Constructor construct;
        Class[] parameters = new Class[] {JSONObject.class};
        try {
            construct = c.getConstructor(parameters);
            result = (Operation) construct.newInstance(j);
        }
        catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

        return result;
    }
    */
    
    private static Object getFieldObject(Field f, Object o) {
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
        t.myTS.myBoolean2DArray = new boolean[][] {new boolean[] {true, true}, new boolean[] {false, false}};
        t.print();
        System.out.println(toJSON(t));
    }
    
    public static class TestClass extends Message {
        public long myLong;
        public double myDouble;
        public String myString;
        public TestSubClass myTS;
    }
    
    public static class TestSubClass {
        public String mySubString;
        public boolean[] myBooleanArray;
        public boolean[][] myBoolean2DArray;
    }
        
}
