/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosapi.message;


import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;

@MessageType(string = "rosapi/MessageDetails")
public class MessageDetails extends Message {
    public TypeDef[] typedefs;
}
