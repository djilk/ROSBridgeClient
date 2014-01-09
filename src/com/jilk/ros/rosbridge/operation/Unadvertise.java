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
@MessageType(string = "unadvertise")
public class Unadvertise extends Operation {
    public String topic;

    public Unadvertise() {}
    
    public Unadvertise(String topic) {
        this.topic = topic;
    }    
}
