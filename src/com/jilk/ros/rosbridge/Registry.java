/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import java.util.HashMap;
import java.util.Map;
import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;

/**
 *
 * @author David J. Jilk
 */
public class Registry {
    private static Map<String, String> topics = new HashMap<String, String>();
    private static Map<String, String> serviceArgs = new HashMap<String, String>();
    private static Map<String, String> serviceResults = new HashMap<String, String>();
    
    public static void registerTopic(String topic, Class messageClass) {
        register(topics, topic, messageClass);
    }
    
    public static String lookupTopic(String topic) {
        return lookup(topics, topic);
    }
    
    public static void registerServiceArgs(String service, Class messageClass) {
        register(serviceArgs, service, messageClass);
    }
    
    public static String lookupServiceArgs(String service) {
        return lookup(serviceArgs, service);
    }
    
    public static void registerServiceResults(String service, Class messageClass) {
        register(serviceResults, service, messageClass);
    }
    
    public static String lookupServiceResults(String service) {
        return lookup(serviceResults, service);
    }
    
    private static void register(Map<String, String> registry, String key, Class messageClass) {
        Message.register(messageClass);
        registry.put(key, ((MessageType) messageClass.getAnnotation(MessageType.class)).string());
    }
    
    private static String lookup(Map<String, String> registry, String key) {
        return registry.get(key);
    }    
}
