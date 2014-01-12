/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.Clock;
import com.jilk.ros.message.TimePrimitive;
import com.jilk.ros.rosbridge.MessageHandler;
import com.jilk.ros.rosbridge.ROSBridgeClient;

/**
 *
 * @author David J. Jilk
 */
public class Example implements MessageHandler {
    
    public Example() {}
    
    @Override
    public void onMessage(String id, Message message) {
        System.out.println("ROSBridgeExample received message, id#" + id);
        message.print();
    }
    
    public static void main(String[] args) {        
        ROSBridgeClient client = new ROSBridgeClient("ws://162.243.238.80:9090");
        client.connect();
        
        client.subscribe("/clock", new Example(), Clock.class);
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
    
    // 1/11/14: have not run this for test yet.
    public static void testTopic() {
        ROSBridgeClient client = new ROSBridgeClient("ws://162.243.238.80:9090");
        client.connect();
        Topic<Clock> clockTopic = new Topic<Clock>("/clock", Clock.class, client);
        clockTopic.subscribe();
        Clock cl = null;
        try {
            cl = clockTopic.take(); // just gets one
        }
        catch (InterruptedException ex) {}
        cl.print();
        cl.clock.nsecs++;
        clockTopic.unsubscribe();
        clockTopic.advertise();
        clockTopic.publish(cl);
        clockTopic.unadvertise();
        client.disconnect();
    }
}
