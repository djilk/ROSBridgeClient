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

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "wrapper")
public class Wrapper extends Operation {
    @Indicator public String op;
    @Indicated public Operation msg;
    
    public Wrapper() {}
}
