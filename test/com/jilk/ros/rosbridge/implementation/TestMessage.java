/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.implementation;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "std_msgs/Test")
public class TestMessage extends Message {
    public String testName;
    public int testInt;
    public double testDouble;
    public short[] testShortArray;
}
