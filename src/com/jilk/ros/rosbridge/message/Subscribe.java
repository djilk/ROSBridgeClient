/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.message;

import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "subscribe")
public class Subscribe extends Operation {
    public String topic;
    public String type;
    public int throttle_rate;
    public int queue_length;
    public int fragment_size;
    public String compression;
    
    public Subscribe() {}
    
    public Subscribe(String topic, String type) {
        this.topic = topic;
        this.type = type;
    }
    
    public static void main(String[] args) {
        Subscribe s = new Subscribe();
        s.print();
        s = new Subscribe("mytopic", "mytype");
        s.print();
    }
}
