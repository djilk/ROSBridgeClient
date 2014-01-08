/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "std_msgs/Time")
public class TimePrimitive extends Message {
    public long secs;
    public long nsecs;
}
