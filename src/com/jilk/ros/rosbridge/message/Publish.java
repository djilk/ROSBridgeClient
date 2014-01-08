/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.message;

import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "publish")
public class Publish extends Operation {
    public String topic;
    public Message msg;
    
    public Publish() {}
    
    public Publish(String topic, Message msg) {
        this.topic = topic;
        this.msg = msg;
    }            
}
