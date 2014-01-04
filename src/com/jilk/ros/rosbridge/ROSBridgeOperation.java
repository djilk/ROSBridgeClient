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
public class ROSBridgeOperation {
    private static Long uid = 0L;
    
    public String op;
    public String id;
    
    ROSBridgeOperation(String op) {
        this.op = op;
        this.id = nextId();
    }
    
    ROSBridgeOperation(JSONObject j) {
        op = (String) j.get("op");
    }
        
    static synchronized String nextId() {
        String result = uid.toString();
        uid++;
        return result;
    }
}
