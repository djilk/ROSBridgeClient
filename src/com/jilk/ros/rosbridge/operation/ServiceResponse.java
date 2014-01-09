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
@MessageType(string = "service_response")
public class ServiceResponse extends Operation {
    public String service;
    public Message[] values; // ut oh. I'm thinking we probably can mostly use the Indicator mechanism,
                             // but will have to do some sort of array -> object mapping procedure. The
                             // call to a service would need to have the reverse operation performed - take
                             // a class structure and turn it into an array. Really the array and the class
                             // structure are similar but the former does not have field names. There is also
                             // the issue of field order when looping - if they are consistently reversed that's
                             // fine, but if they're random then not so much.

    public ServiceResponse() {}
    
    public ServiceResponse(String service) {
        this.service = service;
    }    
}
