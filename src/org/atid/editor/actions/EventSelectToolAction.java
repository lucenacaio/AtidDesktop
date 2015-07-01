/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.editor.actions;

import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import org.atid.editor.Root;
import org.atid.util.GraphicsTools;

/**
 *
 * @author lti
 */
public class EventSelectToolAction  extends AbstractAction  {
     private Root root;

    public EventSelectToolAction(Root root) {
        this.root = root;
        String name = "Event";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("atid/event16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_1);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("D"));
    }

    public void actionPerformed(ActionEvent e) {
        root.selectTool_Event();
    }
}
