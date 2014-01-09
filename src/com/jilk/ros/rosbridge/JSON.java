/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.indication.Indication;

// temporary - for tests
import com.jilk.ros.rosbridge.operation.*;
import com.jilk.ros.message.*;

// The slightly crazy abstractions here are designed to isolate knowledge of
//    the JSON library and data types from the Operation details of rosbridge.
//    Why is this important?  A few reasons I can see.  First, we might want
//    to change JSON libraries and this encapsulates all use of JSON-simple.
//    Second, as much as possible I would like the semantics of the rosbridge
//    protocol to be encapsulated in the Operation and its subclasses rather
//    than in a module that is essentially about serialization.
//
//    Unfortunately the hierarchical Message abstraction is a bit broken at the
//    

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
        JSONObject joUnwrapped = toJSONObject(json);
        JSONObject jo = wrap(joUnwrapped, c);  // a hack to make the hierarchy homogeneous
        return toMessage(jo, c);
    }
    
    private static JSONObject toJSONObject(Object o) {
        JSONObject result = new JSONObject();
        for (Field f : o.getClass().getFields()) {
            Class c = f.getType();
            Object fieldObject = getFieldObject(f, o);
            Object resultObject = null;
            if (fieldObject != null) {
                if (isJSONPrimitive(c))
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
                if (isJSONPrimitive(arrayClass))
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
    
    private static JSONObject wrap(JSONObject jo, Class c) {
        JSONObject result = new JSONObject();
        String indicatorName = Indication.getIndicatorName(c);
        String indicatedName = Indication.getIndicatedName(c);
        result.put(indicatorName, jo.get(indicatorName));
        result.put(indicatedName, jo);
        return result;
    }
            
    private static Message toMessage(JSONObject jo, Class c) {
        try {
            Message result = (Message) c.newInstance();
            for (Field f : c.getFields()) {
                Object lookup = jo.get(f.getName());
                Object value;
                if (lookup != null) {
                    if (lookup.getClass().equals(JSONObject.class)) {
                        Class fc = f.getType();
                        if (Indication.isIndicated(f))
                            fc = Indication.getIndication(result,
                                    (String) jo.get(Indication.getIndicatorName(c)));
                        value = toMessage((JSONObject) lookup, fc);
                    }
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
    
    private static boolean isJSONPrimitive(Class c) {
        return (c.isPrimitive() ||
                c.equals(String.class) ||
                c.equals(Integer.class) ||
                c.equals(Long.class) ||
                c.equals(Double.class));        
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

        Operation.initialize();
        Message.register(Clock.class);
        Publish p = new Publish();
        p.topic = "rosgraph_msgs/Clock";
        Clock c = new Clock();
        c.data = new TimePrimitive();
        c.data.secs = 1000;
        c.data.nsecs = 999999999;
        p.msg = c;
        p.print();
        String json = toJSON(p);
        System.out.println(json);
        Message p2 = toMessage(json, Wrapper.class);
        p2.print();
        
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
