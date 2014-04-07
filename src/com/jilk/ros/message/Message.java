/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "message")
public abstract class Message {
    
    // Some requirements about message types:
    //   - Every field must be explicitly designated as public
    //   - Every field that is not a primitive or near-primitive must be another Message class
    //   - If there is a non-empty constructor, you must also have an empty constructor
    
    public static void register(Class c, Map<String, Class> messageClasses) {
        try {
            typecheck(c);

            // Must register the class and not have duplicate
            // This is not recursive because only the top level message class
            //   needs to be determined from the string - others are top-down.
            String messageString = getMessageType(c);
            Class existingClass = messageClasses.get(messageString);
            if (existingClass != null && !existingClass.equals(c))
                throw new MessageException("Message String \'" + messageString +
                    "\' is assigned to two different classes (" +
                        c.getName() + " and " + existingClass.getName() + ")");
            messageClasses.put(messageString, c);        
        }
        catch (MessageException ex) {
            // should be changed to be a hooked method to give library user control
            System.out.println(ex.getMessage());
        }
    }
    
    public static String getMessageType(Class c) {
        return ((MessageType) c.getAnnotation(MessageType.class)).string();
    }    
    
    // this has never been used or tested but kind of belongs here
    // commented out because it uses ReflectiveOperationException which is not available on Android
    /*
    public static Message newInstance(String className) {
        try {
            Class messageClass = Class.forName(className);
            if (Message.class.isAssignableFrom(messageClass))
                return (Message) messageClass.newInstance();
            else throw new ClassCastException();
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Unable to create message of class \'" + className + "\'.", ex);
        }
    }
    */
    
    // Could probably do more checking here, but not sure what right now
    private static void typecheck(Class c) throws MessageException {
        
        // Must inherit from Message
        if (!Message.class.isAssignableFrom(c))
            throw new MessageException("Class \'" + c.getName() +
                    "\' does not extend Message");

        // Must have the MessageType annotation
        if (getMessageType(c) == null)
            throw new MessageException("Class \'" + c.getName() +
                    "\' is missing the MessageType annotation");
                
        // All fields must also be valid Message classes
        // Note that this also serves to force-load all the message classes
        //      so that they get registered
        for (Field f : c.getFields()) {
            Class fc = f.getType();
            if (fc.isArray()) {
                Class ac = fc.getComponentType(); 
                if (!isPrimitive(ac))
                    typecheck(ac);
            }
            else if (!isPrimitive(fc))
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
                if (isPrimitive(c))
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
                if (isPrimitive(arrayClass))
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
    
    public static boolean isPrimitive(Class c) {
        return (c.isPrimitive() ||
                c.equals(String.class) ||
                Number.class.isAssignableFrom(c) ||
                c.equals(Boolean.class));        
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
    
    public void copyFrom(Message source) {
        try {
            if (source.getClass() != getClass())
                throw new RuntimeException("Attempt to copy non-matching classes");
            for (Field f : getClass().getFields()) {
                Class fc = f.getType();
                if (fc.isArray())
                    throw new RuntimeException("copyFrom - array types not implemented");
                else if (!isPrimitive(fc))
                    ((Message) f.get(this)).copyFrom((Message) f.get(source));
                else {
                    Object value = f.get(source);
                    f.set(this, value);
                }
            }
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException ("copyFrom error", ex);
        }
        catch (ClassCastException ex) {
            throw new RuntimeException ("copyFrom error", ex);
        }
        // ReflectiveOperationException is not available on Android (Java 1.6)
        /*
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException ("copyFrom error", ex);
        }
        */
    }
    
}
