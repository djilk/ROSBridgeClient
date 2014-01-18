/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosapi.message;


import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;

@MessageType(string = "rosapi/Topic")
public class Topic extends Message {
    public String topic;
    
    public Topic() {}
    
    public Topic(String topic) {
        this.topic = topic;
    }
}