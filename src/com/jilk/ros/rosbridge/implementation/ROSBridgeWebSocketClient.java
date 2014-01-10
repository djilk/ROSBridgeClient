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
import com.jilk.ros.rosbridge.operation.Publish;
import com.jilk.ros.rosbridge.operation.ServiceResponse;

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
        Operation operation = Operation.toOperation(message);
        Message msg = null;
        String handlerKey = null;
        if (operation instanceof Publish) {
            Publish p = (Publish) operation;
            handlerKey = p.topic;
            msg = p.msg;
        }
        else if (operation instanceof ServiceResponse) {
            ServiceResponse r = ((ServiceResponse) operation);
            handlerKey = r.service;
            msg = r.values;
        }
        // later we will add clauses for Fragment, PNG, and Status. When rosbridge has it, we'll have one for service requests.

        if (handlerKey != null)
            Registry.lookupHandler(handlerKey).handle(operation.id, msg);
        else operation.print();
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
    
    public void send(Operation operation) {
        send(operation.toJSON());
    }
}
