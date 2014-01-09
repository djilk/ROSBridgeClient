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
@MessageType(string = "set_level")
public class SetStatusLevel {
    public String level;
    
    public SetStatusLevel() {}
    
    public SetStatusLevel(String level) {
        this.level = null;
        if ("none".equals(level) ||
                "warning".equals(level) ||
                "error".equals(level) ||
                "info".equals(level))
            this.level = level;
    }
}
