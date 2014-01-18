/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosapi.message;


import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;

@MessageType(string = "rosapi/Service")
public class Service extends Message {
    public String service;
    
    public Service() {}
    
    public Service(String service) {
        this.service = service;
    }
}