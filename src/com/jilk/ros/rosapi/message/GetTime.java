/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosapi.message;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;
import com.jilk.ros.message.TimePrimitive;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "rosapi/GetTimeResponse")
public class GetTime extends Message {
    public TimePrimitive time;
}
