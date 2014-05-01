/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 * 
 * This file is part of the Java ROSBridge Client.
 *
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 * 
 */
package com.jilk.ros.rosbridge.implementation;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.jilk.ros.rosbridge.FullMessageHandler;
import com.jilk.ros.rosbridge.operation.Operation;
import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.operation.Publish;
import com.jilk.ros.rosbridge.operation.ServiceResponse;
import com.jilk.ros.ROSClient;
import org.java_websocket.framing.CloseFrame;

import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.net.Socket;


public class ROSBridgeWebSocketClient extends WebSocketClient {
    private Registry<Class> classes;
    private Registry<FullMessageHandler> handlers;
    private boolean debug;
    private ROSClient.ConnectionStatusListener listener;
    
    ROSBridgeWebSocketClient(URI serverURI) {
        super(serverURI);
        classes = new Registry<Class>();
        handlers = new Registry<FullMessageHandler>();
        Operation.initialize(classes);  // note, this ensures that the Message Map is initialized too
        listener = null;
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
    
    public void setListener(ROSClient.ConnectionStatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (listener != null)
            listener.onConnect();
    }

    @Override
    public void onMessage(String message) {
        if (debug) System.out.println("<ROS " + message);
        //System.out.println("ROSBridgeWebSocketClient.onMessage (message): " + message);
        Operation operation = Operation.toOperation(message, classes);
        //System.out.println("ROSBridgeWebSocketClient.onMessage (operation): ");
        //operation.print();
        
        FullMessageHandler handler = null;
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
        else if (debug) {
            System.out.print("No handler: id# " + operation.id + ", ");
            if (operation instanceof Publish)
                System.out.println("Publish " + ((Publish) operation).topic); 
            else if (operation instanceof ServiceResponse)
                System.out.println("Service Response " + ((ServiceResponse) operation).service); 
            //operation.print();
        }
    }
       
    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (listener != null) {
            boolean normal = (remote || (code == CloseFrame.NORMAL));
            listener.onDisconnect(normal, reason, code);
        }
    }

    @Override
    public void onError(Exception ex) {
        if (listener != null)
            listener.onError(ex);
        else ex.printStackTrace();
    }
    
    // There is a bug in V1.2 of java_websockets that seems to appear only in Android, specifically,
    //    it does not shut down the thread and starts using gobs of RAM (symptom is frequent garbage collection).
    //    This method goes into the WebSocketClient object and hard-closes the socket, which causes the thread
    //    to exit (note, just interrupting the thread does not work).
    @Override
    public void closeBlocking() throws InterruptedException {
        super.closeBlocking();
        try {
            Field channelField = this.getClass().getSuperclass().getDeclaredField("channel");
            channelField.setAccessible(true);
            SocketChannel channel = (SocketChannel) channelField.get(this);
            if (channel != null && channel.isOpen()) {
                Socket socket = channel.socket();
                if (socket != null)
                        socket.close();
            }
        }
        catch (Exception ex) {
            System.out.println("Exception in Websocket close hack.");
            ex.printStackTrace();
        }
    }
    
    
    public void send(Operation operation) {
        String json = operation.toJSON();
        if (debug) System.out.println("ROS> " + json);
        send(json);
    }
    
    public void register(Class<? extends Operation> c,
            String s,
            Class<? extends Message> m,
            FullMessageHandler h) {
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
