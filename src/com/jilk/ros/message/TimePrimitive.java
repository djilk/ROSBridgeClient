/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "time")
public class TimePrimitive extends Message {
    public int secs;  // when requesting this format from ROSbridge, it uses 'sec' (no 's')
    public int nsecs; // when requesting this format from ROSbridge, it uses 'nsec'
}
