/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.indication.*;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "service_response")
public class ServiceResponse extends Operation {
    @Indicator public String service;
    public boolean result;
    @Indicated public Message values;

    public ServiceResponse() {}
    
    public ServiceResponse(String service) {
        this.service = service;
    }    
}
