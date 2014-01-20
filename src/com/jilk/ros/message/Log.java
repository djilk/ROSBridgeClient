/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "rosgraph_msgs/Log")
public class Log extends Message {
    public Header header;
    public byte level;
    public String name;
    public String msg;
    public String file;
    public String function;
    public long line;
    public String[] topics;
}
