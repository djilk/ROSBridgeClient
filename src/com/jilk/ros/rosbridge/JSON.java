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
        JSONObject jo = convertObjectToJSONObject(m);
        return jo.toJSONString();
    }
    
    public static Message toMessage(String json, Class c) {
        JSONObject joUnwrapped = convertStringToJSONObject(json);
        JSONObject jo = wrap(joUnwrapped, c);  // a hack to make the hierarchy homogeneous
        return convertJSONObjectToMessage(jo, c);
    }
    
    private static JSONObject convertObjectToJSONObject(Object o) {
        JSONObject result = new JSONObject();
        for (Field f : o.getClass().getFields()) {
            Object fieldObject = getFieldObject(f, o);
            if (fieldObject != null) {
                Object resultObject;
                if (Indication.asArray(f))
                    resultObject = convertObjectToJSONArray(fieldObject);
                else resultObject = convertElementToJSON(fieldObject);
                result.put(f.getName(), resultObject);
            }
        }
        return result;
    }
    
    private static JSONArray convertArrayToJSONArray(Object array) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < Array.getLength(array); i++) {
            Object elementObject = Array.get(array, i);
            if (elementObject != null) {
                Object resultObject = convertElementToJSON(elementObject);
                result.add(resultObject);
            }
        }        
        return result;
    }
    
    private static JSONArray convertObjectToJSONArray(Object o) {
        JSONArray result = new JSONArray();
        for (Field f : o.getClass().getFields()) {
            Object fieldObject = getFieldObject(f, o);
            if (fieldObject != null) {
                Object resultObject = convertElementToJSON(fieldObject);
                result.add(resultObject);
            }
        }
        return result;
    }
            
    private static Object convertElementToJSON(Object elementObject) {
        Class elementClass = elementObject.getClass();
        Object resultObject;
        if (Message.isPrimitive(elementClass))
            resultObject = elementObject;
        else if (elementClass.isArray())  
            resultObject = convertArrayToJSONArray(elementObject);
        else
            resultObject = convertObjectToJSONObject(elementObject);
        return resultObject;
    }
    
    private static JSONObject convertStringToJSONObject(String json) {
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
    
    // A bit of a hack to create a consistent hierarchy with jsonbridge operations
    private static JSONObject wrap(JSONObject jo, Class c) {
        JSONObject result = new JSONObject();
        String indicatorName = Indication.getIndicatorName(c);
        String indicatedName = Indication.getIndicatedName(c);
        result.put(indicatorName, jo.get(indicatorName));
        result.put(indicatedName, jo);
        return result;
    }
            
    private static Message convertJSONObjectToMessage(JSONObject jo, Class c) {
        try {
            Message result = (Message) c.newInstance();
            for (Field f : c.getFields()) {
                Class fc = getFieldClass(result, jo, f);
                Object lookup = jo.get(f.getName());
                if (lookup != null) {
                    Object value = convertElementToField(lookup, fc, f);
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

    private static Object convertElementToField(Object element, Class fc, Field f) {
        Object value;
        if (element.getClass().equals(JSONObject.class)) {
            value = convertJSONObjectToMessage((JSONObject) element, fc);
        }
        else if (element.getClass().equals(JSONArray.class)) {
            if (Indication.asArray(f))
                value = convertJSONArrayToMessage((JSONArray) element, fc);
            else value = convertJSONArrayToArray((JSONArray) element, fc);
        }
        else
            value = convertJSONPrimitiveToPrimitive(element, fc);
         
       return value;        
    }
    
    private static Object convertJSONArrayToArray(JSONArray ja, Class c) {
        Object result = Array.newInstance(c, ja.size());
        for (int i = 0; i < ja.size(); i++) {
            Object lookup = ja.get(i);
            Object value = null;
            if (lookup != null) {
                if (lookup.getClass().equals(JSONObject.class))
                    value = convertJSONObjectToMessage((JSONObject) lookup, c);
                else if (lookup.getClass().equals(JSONArray.class))  // this is not actually allowed in ROS
                    value = convertJSONArrayToArray((JSONArray) lookup, c.getComponentType());
                else 
                    value = convertJSONPrimitiveToPrimitive(lookup, c);
                Array.set(result, i, value);
            }
        }
        
        return result;
    }
    
    private static Message convertJSONArrayToMessage(JSONArray ja, Class c) {
        try {
            Message result = (Message) c.newInstance();
            int arrayIndex = 0;
            for (Field f : c.getFields()) {
                Class fc = getFieldClass(result, null, f);
                Object lookup = ja.get(arrayIndex++);         // yes we are assuming that the fields are delivered in order
                if (lookup != null) {
                    Object value = convertElementToField(lookup, fc, f);
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
    
    // Note that this is not checking ranges
    public static Object convertJSONPrimitiveToPrimitive(Object o, Class c) {
        Object result = o;
        if (c.isPrimitive() || Number.class.isAssignableFrom(c)) {
            if (c.equals(double.class) || c.equals(Double.class))
                result = new Double(((Number) o).doubleValue());
            else if (c.equals(float.class) || c.equals(Float.class))
                result = new Float(((Number) o).floatValue());
            else if (c.equals(long.class) || c.equals(Long.class))
                result = new Long(((Number) o).longValue());
            else if (int.class.equals(c) || c.equals(Integer.class))
                result = new Integer(((Number) o).intValue());
            else if (c.equals(short.class) || c.equals(Short.class))
                result = new Short(((Number) o).shortValue());
            else if (c.equals(byte.class) || c.equals(Byte.class))
                result = new Byte(((Number) o).byteValue());
        }
        
        return result;
    }
    
    public static Class getFieldClass(Message parent, JSONObject jo, Field f) {
        Class fc;
        fc = f.getType();
        if (fc.isArray())
            fc = f.getType().getComponentType();
        if (Indication.isIndicated(f) && (jo != null))
            fc = Indication.getIndication(parent,
                    (String) jo.get(Indication.getIndicatorName(parent.getClass())));
        return fc;
    }    
    
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
        t.myInt = 99;
        t.myLong = 100000000;
        t.myDouble = 3.14159;
        t.myString = "Test String";
        t.myTS = new TestSubClass();
        t.myTS.mySubString = "Test Sub String";
        t.myTS.myBooleanArray = new boolean[] {false, true};
        t.myTS.myBoolean2DArray = new boolean[][] {new boolean[] {true, true}, new boolean[] {false, false}};
        System.out.println("Test Class");
        t.print();
        System.out.println("Test Class as JSON");
        String json = toJSON(t);
        System.out.println(json);

        Operation.initialize();
        Registry.registerTopic("/clock", Clock.class);
        Publish p = new Publish();
        p.topic = "/clock";
        Clock c = new Clock();
        c.data = new TimePrimitive();
        c.data.secs = 1000;
        c.data.nsecs = 999999999;
        p.msg = c;
        
        System.out.println("Publish Operation");
        p.print();
        json = toJSON(p);
        System.out.println("Publish Operation as JSON");
        System.out.println(json);
        
        System.out.println("Publish Operation converted back from JSON");
        Message p2 = Operation.toOperation(json);
        p2.print();
        
        Registry.registerServiceArgs("/srv", Test.class);
        Test t1 = new Test();
        t1.testName = "Test Name";
        t1.testInt = 77;
        t1.testDouble = 2.71828;
        t1.testShortArray = new short[] {1, 2, 3, 4};
        CallService cs = new CallService("/srv");
        cs.args = t1;
        
        System.out.println("CallService Operation");
        cs.print();
        json = toJSON(cs);

        System.out.println("CallService Operation as JSON");
        System.out.println(json);

        System.out.println("CallService Operation converted back from JSON");
        Message cs2 = Operation.toOperation(json);
        cs2.print();        
    }
    
    public static class TestClass extends Message {
        public long myInt;
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
