/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.jilk.ros.rosbridge.operation.Wrapper;

// tests
import com.jilk.ros.rosbridge.operation.Subscribe;
import com.jilk.ros.rosbridge.operation.Operation;

/**
 *
 * @author David J. Jilk
 */
public class ROSBridgeWebSocketClient extends WebSocketClient {
    
    ROSBridgeWebSocketClient(URI serverURI) {
            super(serverURI);
    }
    
    public static ROSBridgeWebSocketClient create(String URIString) {
        ROSBridgeWebSocketClient client = null;
        try {
            URI uri = new URI(URIString);
            client = new ROSBridgeWebSocketClient(uri);            
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return client;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection open.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        Operation op = Operation.toOperation(message);
        op.print();
    }
       
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed.");
        if (remote)
            System.out.println("Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    //  1/7: coded but untested - need to try with real ROS
    //  1/8: still haven't tested in full
    
    public static void main( String[] args ) throws URISyntaxException {
        Operation.initialize();
        // register all the topics and service results here.
        // when rosbridge allows clients to offer services, also need to register service args
        ROSBridgeWebSocketClient c = ROSBridgeWebSocketClient.create("ws://162.243.238.80:9090");
        if (c != null) {
            try {
                if (c.connectBlocking()) {
                    String msg;
                    //msg = "{\"op\": \"subscribe\", \"topic\": \"/clock\", \"type\": \"rosgraph_msgs/Clock\"}";
                    Subscribe s = new Subscribe("/clock", "rosgraph_msgs/Clock");
                    msg = JSON.toJSON(s);
                    System.out.println("subscribe: " + msg);
                    c.send(msg);
                    try {Thread.sleep(20000);} catch(Exception ex) {}

                    try {
                        c.closeBlocking();
                    }
                    catch (InterruptedException ex) {}
                }
            }
            catch (InterruptedException ex) {}
        }
    }    
}
