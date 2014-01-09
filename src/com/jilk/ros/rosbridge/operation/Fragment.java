/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "fragment")
public class Fragment {
    public String data;
    public int num;
    public int total;
    
    public Fragment() {}
    
    public Fragment(String data, int num, int total) {
        this.data = data;
        this.num = num;
        this.total = total;
    }
}
