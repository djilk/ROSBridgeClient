/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "png")
public class PNG extends Operation {
    public String data;
    public Integer num;     // use Integer for optional items
    public Integer total;   // use Integer for optional items
    
    public PNG() {}
    
    public PNG(String data) {
        this.data = data;
    }
    
    public PNG(String data, int num, int total) {
        this.data = data;
        this.num = num;
        this.total = total;
    }
}
