/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.Clock;
import com.jilk.ros.message.TimePrimitive;

/**
 *
 * @author David J. Jilk
 */
public class ROSBridgeExample implements MessageHandler {
    
    public ROSBridgeExample() {}
    
    @Override
    public void handle(String id, Message message) {
        System.out.println("ROSBridgeExample received message, id#" + id);
        message.print();
    }
    
    //  1/7: coded but untested - need to try with real ROS
    //  1/8: still haven't tested in full
    
    public static void main(String[] args) {        
        ROSBridgeClient client = new ROSBridgeClient("ws://162.243.238.80:9090");
        client.connect();
        
        client.subscribe("/clock", new ROSBridgeExample(), Clock.class);
        try {Thread.sleep(20000);} catch(InterruptedException ex) {}
        
        /*
        client.advertise("/dave", Clock.class);
        Clock c = new Clock();
        c.clock = new TimePrimitive();
        c.clock.secs = 314159;
        c.clock.nsecs = 271828;
        client.publish("/dave", c);
        c.clock.nsecs++;
        client.publish("/dave", c);
        client.unadvertise("/dave");
        */
        client.disconnect();
    }            
}
