/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.message;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "operation")
public class Operation extends Message {
    private static Long uid = 0L;

    public String op;
    public String id;
    
    Operation() {
        this.op = ((MessageType) this.getClass().getAnnotation(MessageType.class)).string();
        this.id = nextId();
    }
    
    private static synchronized String nextId() {
        String result = uid.toString();
        uid++;
        return result;
    }
    
    // Operation subclasses are registered directly rather than through the
    //     passive static initializer approach, because they can be at the root
    //     of an inbound message and otherwise we wouldn't find them.
    public static void initialize() {
        Message.register(Operation.class);
        Message.register(Publish.class);
        Message.register(Subscribe.class);
    }
    
    public static void main(String[] args) {
        Operation.initialize();
        System.out.println(Message.lookup("subscribe").getName());
        System.out.println(Message.lookup("publish").getName());
    }
}
