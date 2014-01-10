/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "rosgraph_msgs/Clock")
public class Clock extends Message {
    public TimePrimitive clock;
}
