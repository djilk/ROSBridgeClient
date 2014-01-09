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
@MessageType(string = "status")
public class Status {
    String level;
    String msg;
    
    public Status() {}
    
    public Status(String level, String msg) {
        this.level = level;
        this.msg = msg;
    }
}
