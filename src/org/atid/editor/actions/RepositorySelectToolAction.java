/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.atid.editor.Root;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caiolucena
 */
public class RepositorySelectToolAction extends AbstractAction{
    private Root root;

    public RepositorySelectToolAction(Root root) {
        this.root = root;
        String name = "Repository";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("atid/repository16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_2);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("D"));
    }

    public void actionPerformed(ActionEvent e) {
        root.selectTool_Repository();
    }
}
