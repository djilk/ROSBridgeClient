/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.MessageType;

/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "auth")
public class Authenticate extends Operation {
    public String mac;
    public String client;
    public String dest;
    public String rand;
    public int t;
    public String level;
    public int end;
    
    public Authenticate() {}
    
    public Authenticate(
            String mac,
            String client,
            String dest,
            String rand,
            int t,
            String level,
            int end)
    {
        this.mac = mac;
        this.client = client;
        this.dest = dest;
        this.rand = rand;
        this.t = t;
        this.level = level;
        this.end = end;

        this.id = null; // even though id is on EVERY OTHER operation type
    }
    
}
