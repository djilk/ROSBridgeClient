/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "std_msgs/Test")
public class Test extends Message {
    public String testName;
    public int testInt;
    public double testDouble;
    public short[] testShortArray;
}
