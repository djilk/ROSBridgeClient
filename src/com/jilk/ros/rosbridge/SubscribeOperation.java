/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import org.json.simple.JSONObject;

/**
 *
 * @author David J. Jilk
 */
public class SubscribeOperation extends ROSBridgeOperation {
    public String topic;
    public String type;
    
    SubscribeOperation(String topic, String type) {
        super("subscribe");
        this.topic = topic;
        this.type = type;
    }

    public SubscribeOperation(JSONObject j) {
        super(j);
        topic = (String) j.get("topic"); // could automate this
        topic = (String) j.get("type"); // could automate this
    }    
}
