/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.message;

/**
 *
 * @author David J. Jilk
 */
public class MessageException extends Exception {

    public MessageException(String message) {
        super(message);
    }
    
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }    
}

