/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.Registry;
import com.jilk.ros.rosbridge.indication.*;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "call_service")
public class CallService extends Operation {
    @Indicator public String service;
    @Indicated @AsArray public Message args;  
    public Integer fragment_size; // use Integer for optional items
    public String compression;

    public CallService() {}
    
    public CallService(String service) {
        this.service = service;
    }    
    
    @Indicate
    public Class indicate(String s) {
        return Message.lookup(Registry.lookupServiceArgs(s));
    }
}
