/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.observer;

import java.util.EventObject;
import org.atid.editor.RootPflow;

/**
 *
 * @author Willa
 */
public class RootPflowEvent extends EventObject{

    public RootPflowEvent(RootPflow source) {
        super(source);
    }
    
}
