/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.observer;

import java.util.EventListener;
import org.atid.editor.Root;
import org.atid.editor.RootPflow;

/**
 *
 * @author Willa
 */
public interface RootPflowListener extends EventListener{
    
    public void clicou(RootPflowEvent r);
    
}
