/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.implementation;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.jilk.ros.rosbridge.operation.Operation;
import com.jilk.ros.message.Message;
import com.jilk.ros.MessageHandler;
import com.jilk.ros.rosbridge.operation.Publish;
import com.jilk.ros.rosbridge.operation.ServiceResponse;

/**
 *
 * @author David J. Jilk
 */
public class ROSBridgeWebSocketClient extends WebSocketClient {
    private Registry<Class> classes;
    private Registry<MessageHandler> handlers;
    private boolean debug;
    
    ROSBridgeWebSocketClient(URI serverURI) {
        super(serverURI);
        classes = new Registry<Class>();
        handlers = new Registry<MessageHandler>();
        Operation.initialize(classes);  // note, this ensures that the Message Map is initialized too
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
        //System.out.println("Connection open.");
    }

    @Override
    public void onMessage(String message) {
        if (debug) System.out.println("<ROS " + message);
        //System.out.println("ROSBridgeWebSocketClient.onMessage (message): " + message);
        Operation operation = Operation.toOperation(message, classes);
        //System.out.println("ROSBridgeWebSocketClient.onMessage (operation): ");
        //operation.print();
        
        MessageHandler handler = null;
        Message msg = null;
        if (operation instanceof Publish) {
            Publish p = (Publish) operation;
            handler = handlers.lookup(Publish.class, p.topic);
            msg = p.msg;
        }
        else if (operation instanceof ServiceResponse) {
            ServiceResponse r = ((ServiceResponse) operation);
            handler = handlers.lookup(ServiceResponse.class, r.service);
            msg = r.values;
        }
        // later we will add clauses for Fragment, PNG, and Status. When rosbridge has it, we'll have one for service requests.

        // need to handle "result: null" possibility for ROSBridge service responses
        // this is probably some sort of call to the operation for "validation." Do it
        // as part of error handling.
        
        
        if (handler != null)
            handler.onMessage(operation.id, msg);
        else {
            System.out.println("No handler for message id# " + operation.id);
            //operation.print();
        }
    }
       
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Close codes are documented in class org.java_websocket.framing.CloseFrame
        /*
        System.out.println("Connection closed.");
        if (remote)
            System.out.println("Reason: " + reason);
        */
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
    
    public void send(Operation operation) {
        String json = operation.toJSON();
        if (debug) System.out.println("ROS> " + json);
        send(json);
    }
    
    public void register(Class<? extends Operation> c,
            String s,
            Class<? extends Message> m,
            MessageHandler h) {
        Message.register(m, classes.get(Message.class));
        classes.register(c, s, m);
        if (h != null)
            handlers.register(c, s, h);
    }
    
    public void unregister(Class<? extends Operation> c, String s) {
        handlers.unregister(c, s);
        // Note that there is no concept of unregistering a class - it can get replaced is all
    }
    
    public Class<? extends Message> getRegisteredMessage(String messageString) {
        return classes.lookup(Message.class, messageString);
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
