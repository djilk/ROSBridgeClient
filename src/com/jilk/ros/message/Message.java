/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "message")
public abstract class Message {
    private static Map<String, Class> messageClasses = new HashMap<String, Class>();
    
    public static void register(Class c) {
        try {
            typecheck(c);
            String messageString = ((MessageType) c.getAnnotation(MessageType.class)).string();
            if (messageClasses.get(messageString) != null)
                throw new MessageException("Class \'" + c.getName() +
                    "\' has a duplicate message type: " + messageString);
            messageClasses.put(messageString, c);
        }
        catch (MessageException ex) {
            // should be changed to be a hooked method to give library user control
            System.out.println(ex.getMessage());
        }
    }
    
    public static Class lookup(String messageString) {
        return messageClasses.get(messageString);
    }
    
    // Could probably do more checking here, but not sure what right now
    private static void typecheck(Class c) throws MessageException {
        
        // Must inherit from Message
        if (!Message.class.isAssignableFrom(c))
            throw new MessageException("Class \'" + c.getName() +
                    "\' does not extend Message");

        // Must have the MessageType annotation
        if (c.getAnnotation(MessageType.class) == null)
            throw new MessageException("Class \'" + c.getName() +
                    "\' is missing the MessageType annotation");
                
        // All fields must also be valid Message classes
        // Note that this also serves to force-load all the message classes
        //      so that they get registered
        for (Field f : c.getFields()) {
            Class fc = f.getType();
            if (fc.isArray())
                typecheck(fc.getComponentType());
            else if (!fc.isPrimitive() && !String.class.equals(fc))
                typecheck(fc);
        }
    }
    
    public void print() {
        printMessage(this, "");
    }
    
    private static void printMessage(Object o, String indent) {
        for (Field f : o.getClass().getFields()) {
            Class c = f.getType();
            Object fieldObject = getFieldObject(f, o);
            if (fieldObject != null) {
                if (c.isPrimitive() || c.equals(String.class))
                    System.out.println(indent + f.getName() + ": " + fieldObject);
                else if (c.isArray()) {
                    System.out.println(indent + f.getName() + ": [");                    
                    printArray(fieldObject, indent + "  ");
                    System.out.println(indent + "]");
                }
                else {
                    System.out.println(indent + f.getName() + ":");
                    printMessage(fieldObject, indent + "  ");
                }
            }
        }
    }
    
    private static void printArray(Object array, String indent) {
        Class arrayClass = array.getClass().getComponentType();
        for (int i = 0; i < Array.getLength(array); i++) {
            Object elementObject = Array.get(array, i);
            if (elementObject != null) {
                if (arrayClass.isPrimitive() || arrayClass.equals(String.class))
                    System.out.println(indent + i + ": " + elementObject);
                else if (arrayClass.isArray()) { // this is not actually allowed in ROS
                    System.out.println(indent + i + ": [");                    
                    printArray(elementObject, indent + "  ");
                    System.out.println(indent + "]");
                }
                else {
                    System.out.println(indent + i + ":");
                    printMessage(elementObject, indent + "  ");
                }
            }
        }
        // remember to print array indices
    }
        

    // Copied from com.jilk.ros.rosbridge.JSON
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
    
}
