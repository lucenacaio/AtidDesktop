/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.atid.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.atid.petrinet.Marking;
import org.atid.editor.Root;
import org.atid.editor.commands.SetTokensCommand;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetTokensAction extends AbstractAction {

    private Root root;

    public SetTokensAction(Root root) {
        this.root = root;
        String name = "Set tokens";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("atid/tokens.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("T"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Marking initialMarking = root.getDocument().petriNet.getInitialMarking();
        if (root.getClickedElement() != null) {
            if (root.getClickedElement() instanceof SimpleActivityNode) {
                SimpleActivityNode simpleActivityNode = (SimpleActivityNode) root.getClickedElement();
                int tokens = initialMarking.getTokens(simpleActivityNode);

                String response = JOptionPane.showInputDialog(root.getParentFrame(), "Tokens:", tokens);
                if (response != null) {
                    try {
                        tokens = Integer.parseInt(response);
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(root.getParentFrame(), exception.getMessage() + " is not a number");
                    }

                    if (tokens < 0) {
                        tokens = initialMarking.getTokens(simpleActivityNode); // restore old value
                        JOptionPane.showMessageDialog(root.getParentFrame(), "Number of tokens must be non-negative");
                    }
                }

                if (initialMarking.getTokens(simpleActivityNode) != tokens) {
                    root.getUndoManager().executeCommand(new SetTokensCommand(simpleActivityNode, tokens, initialMarking));
                }
            }
        }
    }
}
