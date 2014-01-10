/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.implementation;

import com.jilk.ros.rosbridge.MessageHandler;
import java.util.HashMap;
import java.util.Map;
import com.jilk.ros.message.Message;

/**
 *
 * @author David J. Jilk
 */
public class Registry {
    private static Map<String, String> topics = new HashMap<String, String>();
    private static Map<String, String> serviceArgs = new HashMap<String, String>();
    private static Map<String, String> serviceResults = new HashMap<String, String>();
    private static Map<String, MessageHandler> handlers = new HashMap<String, MessageHandler>();
    
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
    
    public static void registerHandler(String key, MessageHandler handler) {
        handlers.put(key, handler);
    }
    
    public static MessageHandler lookupHandler(String key) {
        return handlers.get(key);
    }
    
    private static void register(Map<String, String> registry, String key, Class messageClass) {
        Message.register(messageClass);
        registry.put(key, Message.getMessageType(messageClass));
    }
    
    private static String lookup(Map<String, String> registry, String key) {
        return registry.get(key);
    }    
}
