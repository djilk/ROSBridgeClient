/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "call_service")
public class CallService {
    public String service;
    public Message[] args;  // when we add service invocations to rosbridge,
                            //    there will need to be some way to do class
                            //    indication for this array. Presumably tied to
                            //    the service specification somehow.
    public Integer fragment_size; // use Integer for optional items
    public String compression;

    public CallService() {}
    
    public CallService(String service) {
        this.service = service;
    }    
}
