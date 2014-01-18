/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import com.jilk.ros.message.Clock;
import com.jilk.ros.rosapi.message.Empty;
import com.jilk.ros.rosapi.message.Topics;
import com.jilk.ros.rosapi.message.Topic;
import com.jilk.ros.rosapi.message.Type;
import com.jilk.ros.rosapi.message.MessageDetails;
import com.jilk.ros.rosapi.message.TypeDef;
import com.jilk.ros.rosbridge.ROSBridgeClient;

/**
 *
 * @author David J. Jilk
 */
public class Example {
    
    public Example() {}
    
    public static void main(String[] args) {        
        ROSBridgeClient client = new ROSBridgeClient("ws://162.243.238.80:9090");
        client.connect();
        //testTopic(client);
        testService(client);
        client.disconnect();
    }            
    
    public static void testService(ROSBridgeClient client) {
        try {
            System.out.println("Nodes");
            for (String s : client.getNodes())
                System.out.println("    " + s);
            System.out.println("Topics");
            for (String s : client.getTopics()) {
                System.out.println(s + ":");
                client.getTopicMessageDetails(s).print();
            }
            System.out.println("Services");
            for (String s : client.getServices()) {
                System.out.println(s + ":");
                client.getServiceRequestDetails(s).print();
                System.out.println("-----------------");
                client.getServiceResponseDetails(s).print();
            }
        }
        catch (InterruptedException ex) {
            System.out.println("Process was interrupted.");
        }
        /*
        Service<Empty, Topics> topicService =
                new Service<Empty, Topics>("/rosapi/topics", Empty.class, Topics.class, client);
        Service<Topic, Type> typeService =
                new Service<Topic, Type>("/rosapi/topic_type", Topic.class, Type.class, client);
        Service<Type, MessageDetails> messageService =
                new Service<Type, MessageDetails>("/rosapi/message_details", Type.class, MessageDetails.class, client);
        try {
            Topics topics = topicService.callBlocking(new Empty());
            for (String topicString : topics.topics) {
                Topic topic = new Topic();
                topic.topic = topicString;
                Type type = typeService.callBlocking(topic);
                MessageDetails details = messageService.callBlocking(type);
                System.out.println("Topic: " + topic.topic + " Type: " + type.type);
                details.print();
                System.out.println();
            }
            Type type = new Type();
            type.type = "time";
            System.out.print("Single type check on \'time\': ");
            messageService.callBlocking(type).print();
        }
        catch (InterruptedException ex) {
            System.out.println("testService: process was interrupted.");
        }
        */
    }
    
    public static void testTopic(ROSBridgeClient client) {
        com.jilk.ros.Topic<Clock> clockTopic = new com.jilk.ros.Topic<Clock>("/clock", Clock.class, client);
        clockTopic.subscribe();
        try {Thread.sleep(20000);} catch(InterruptedException ex) {}
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
    }
}
