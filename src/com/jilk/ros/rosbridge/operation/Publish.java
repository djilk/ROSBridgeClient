/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.rosbridge.indication.Indicator;
import com.jilk.ros.rosbridge.indication.Indicated;
import com.jilk.ros.rosbridge.indication.Indicate;
import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.implementation.Registry;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "publish")
public class Publish extends Operation {
    
    @Indicator public String topic;
    @Indicated public Message msg;
    
    public Publish() {}
    
    public Publish(String topic, Message msg) {
        this.topic = topic;
        this.msg = msg;
    }
    
    @Indicate
    public Class indicate(String s) {
        return Message.lookup(Registry.lookupTopic(s));
    }
}
