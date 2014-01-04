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
public class PublishOperation extends ROSBridgeOperation {
    public String topic;
    public String msg;
    
    PublishOperation(String topic, String msg) {
        super("publish");
        this.topic = topic;
        this.msg = msg;
    }    
    
    public PublishOperation(JSONObject j) {
        super(j);
        topic = (String) j.get("topic"); // could automate this
        msg = ((JSONObject) j.get("msg")).toJSONString();
    }
}
