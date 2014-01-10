/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "unsubscribe")
public class Unsubscribe extends Operation {
    public String topic;

    public Unsubscribe() {}
    
    public Unsubscribe(String topic) {
        this.topic = topic;
    }    
}
