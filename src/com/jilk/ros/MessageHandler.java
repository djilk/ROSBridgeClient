/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jilk.ros;

import com.jilk.ros.message.Message;

/**
 *
 * @author david_000
 */
public interface MessageHandler<T extends Message> {
    public void onMessage(String id, T message);
}
