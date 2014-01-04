/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
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
        System.out.println( "Connection open." );
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        ROSBridgeOperation op = ROSBridgeJSONObject.operationFactory(message);
        System.out.println("op:" + op.op);
        ROSBridgeJSONObject rboj = new ROSBridgeJSONObject();
        rboj.set(op);
        System.out.println(rboj.toJSONString());
    }
       
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "Connection closed.");
        if (remote)
            System.out.println("Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main( String[] args ) throws URISyntaxException {
        ROSBridgeWebSocketClient c = ROSBridgeWebSocketClient.create("ws://162.243.238.80:9090");
        if (c != null) {
            try {
                if (c.connectBlocking()) {
                    String msg;
                    //msg = "{\"op\": \"subscribe\", \"topic\": \"/clock\", \"type\": \"rosgraph_msgs/Clock\"}";
                    SubscribeOperation s = new SubscribeOperation("/clock", "rosgraph_msgs/Clock");
                    ROSBridgeJSONObject rbjo = new ROSBridgeJSONObject();
                    rbjo.set(s);
                    msg = rbjo.toJSONString();
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
