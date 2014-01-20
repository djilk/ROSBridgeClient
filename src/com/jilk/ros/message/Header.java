/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "std_msgs/Header")
public class Header extends Message {
    public long seq;
    public TimePrimitive stamp;
    public String frame_id;
}
