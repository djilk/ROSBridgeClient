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
@MessageType(string = "subscribe")
public class Subscribe extends Operation {
    public String topic;
    public String type;
    public Integer throttle_rate;   // use Integer for optional items
    public Integer queue_length;    // use Integer for optional items
    public Integer fragment_size;   // use Integer for optional items
    public String compression;
    
    public Subscribe() {}
    
    public Subscribe(String topic, String type) {
        this.topic = topic;
        this.type = type;
    }    
}
