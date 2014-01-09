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
@MessageType(string = "advertise")
public class Advertise extends Operation {
    public String topic;
    public String type;

    public Advertise() {}
    
    public Advertise(String topic, String type) {
        this.topic = topic;
        this.type = type;
    }    
}
