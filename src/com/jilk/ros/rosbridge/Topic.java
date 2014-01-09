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
public class Topic {
    private static Map<String, String> topics = new HashMap<String, String>();
    
    public static void register(String topic, Class messageClass) {
        Message.register(messageClass);
        topics.put(topic, ((MessageType) messageClass.getAnnotation(MessageType.class)).string());
    }
    
    public static String lookup(String topic) {
        return topics.get(topic);
    }
}
