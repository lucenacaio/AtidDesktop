/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.Transition;
import org.atid.util.CachedGraphics2D;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caio
 */
public class AddConditionAction extends AbstractAction{

    private Root root;
       
    public AddConditionAction(Root root) {
        this.root = root;
        String name = "Condition";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        
        if(root.getClickedElement() != null){
            if(root.getClickedElement() instanceof Transition){
                Transition transition = (Transition) root.getClickedElement();
                transition.drawCondition(CachedGraphics2D.getGraphics());
                JOptionPane.showInputDialog(root.getParentFrame(),"Condition: ");
            }
        }
        
        
    }
}
