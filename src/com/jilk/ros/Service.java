/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.MessageHandler;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.operation.CallService;
import com.jilk.ros.rosbridge.operation.ServiceResponse;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/*
 * TODO: 1. create an inner class instead of having three separate hashes.
 *       2. write the basic reflection calls for services, topics, and data types
 *       3. build the "implements" method for TypeDef (compare with Java class)
 *       4. write the verify methods for Service and Topic
 */


/**
 *
 * @author David J. Jilk
 */
public class Service<CallType extends Message, ResponseType extends Message> implements MessageHandler {
    private String service;
    private Class<ResponseType> responseType;
    private Class<CallType> callType;
    private ROSBridgeClient client;
    private Map<String, ResponseType> results;
    private Map<String, CountDownLatch> latches;
    private Map<String, MessageHandler<ResponseType>> handlers;
    
    public Service(String service, Class<CallType> callType, Class<ResponseType> responseType, ROSBridgeClient client) {
        this.service = service;
        this.client = client;
        this.responseType = responseType;
        this.callType = callType;
        results = new HashMap<String, ResponseType>();
        latches = new HashMap<String, CountDownLatch>();
        handlers = new HashMap<String, MessageHandler<ResponseType>>();
    }

    // A result can only be returned once; it is cleared from the hash before 
    //   being sent/returned. This is to ensure that results do not accumulate
    //   indefinitely.  If callers need to keep these around they can set up their 
    //   own hash.
    
    @Override
    public void onMessage(String id, Message response) {        
        System.out.print("Service.onMessage: ");
        response.print();
        MessageHandler<ResponseType> handler = handlers.get(id);
        if (handler != null) {
            clear(id);
            handler.onMessage(id, (ResponseType) response);
        }
        else {
            results.put(id, (ResponseType) response);
            latches.get(id).countDown();
        }
    }
    
    public String call(CallType args) {
        return callImpl(args, null);
    }
    
    public void callWithHandler(CallType args, MessageHandler<ResponseType> responseHandler) {
        callImpl(args, responseHandler);
    }
    
    public ResponseType callBlocking(CallType args) throws InterruptedException {
        return take(call(args));
    }
    
    private String callImpl(CallType args, MessageHandler<ResponseType> responseHandler) {
        client.register(ServiceResponse.class, service, responseType, this);  // do this once on creation?
        CallService cs = new CallService(service, args);
        String id = cs.id;
        results.put(id, null);
        latches.put(id, new CountDownLatch(1));
        handlers.put(id, responseHandler);
        client.send(cs);
        return id;
    }
    
    public ResponseType poll(String id) {
        ResponseType result = results.get(id);
        if (result != null)
            clear(id);
        return result;
    }
    
    public ResponseType take(String id) throws InterruptedException {
        latches.get(id).await();
        ResponseType result = results.get(id);
        clear(id);
        return result;
    }
    
    public boolean verify() {
        // use discovery tools (rosapi) to confirm that
        // this service is actually running and that the data types
        // are correct. 
      return true;
    }
    
    private void clear(String id) {
        results.remove(id);
        latches.remove(id);        
        handlers.remove(id);
    }

}
