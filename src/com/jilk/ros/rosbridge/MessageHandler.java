/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jilk.ros.rosbridge;

import com.jilk.ros.message.Message;

/**
 *
 * @author david_000
 */
public interface MessageHandler {
    public void handle(String id, Message message);
}
