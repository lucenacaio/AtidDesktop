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
package org.atid.editor.commands;

import javax.swing.JOptionPane;
import org.atid.editor.Atid;
import org.atid.petrinet.Marking;
import org.atid.petrinet.Node;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.util.Command;

/**
 * Set label to clicked element
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetLabelCommand implements Command {

    private Node node;
    private String newLabel;
    private String oldLabel;

    public SetLabelCommand(Node node, String newLabel) {
            this.node = node;
            this.newLabel = newLabel;
    }

    public void execute() {
        
        SimpleActivityNode nodeConvert = (SimpleActivityNode) node;
        if(PetriNet.getNewBegin() == null && newLabel.toLowerCase().equals("begin")){
            this.oldLabel = node.getLabel();
            node.setLabel(newLabel);
            PetriNet.setOldBegin(PetriNet.getNewBegin());
            PetriNet.setNewBegin(nodeConvert);
            Marking initialMarking = Atid.getRoot().getCurrentMarking();
            Atid.getRoot().getUndoManager().executeCommand(new AddTokenCommand(nodeConvert, initialMarking));
        } else if(PetriNet.getNewBegin() != null && newLabel.toLowerCase().equals("begin")){
            JOptionPane.showMessageDialog(null, "There is already a 'begin 'activity on the network.");
        } else if(node == PetriNet.getNewBegin() && !newLabel.toLowerCase().equals("begin")){
            PetriNet.setNewBegin(null);
            Marking initialMarking = Atid.getRoot().getCurrentMarking();
            Atid.getRoot().getUndoManager().executeCommand(new RemoveTokenCommand(nodeConvert, initialMarking));
            this.oldLabel = node.getLabel();
            node.setLabel(newLabel);
        }else if(!newLabel.toLowerCase().equals("begin")){
            this.oldLabel = node.getLabel();
            node.setLabel(newLabel);
        }
        
    }

    public void undo() {
       SimpleActivityNode nodeConvert = (SimpleActivityNode) node;
        if(nodeConvert == PetriNet.getNewBegin()){
            PetriNet.setNewBegin(PetriNet.getOldBegin());
            node.setLabel(oldLabel);
        }else{
            node.setLabel(oldLabel);
        }   
            
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set label to " + newLabel;
    }

}
