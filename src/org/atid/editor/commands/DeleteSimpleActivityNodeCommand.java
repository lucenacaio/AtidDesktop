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

import java.util.HashSet;
import java.util.Set;
import org.atid.petrinet.Arc;
import org.atid.petrinet.ArcEdge;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.ReferenceArc;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteSimpleActivityNodeCommand implements Command {

    private SimpleActivityNode simpleActivityNode;
    private Set<Command> deleteAllArcEdges = new HashSet<Command>();

    public DeleteSimpleActivityNodeCommand(SimpleActivityNode simpleActivityNode) {
        this.simpleActivityNode = simpleActivityNode;
        Set<ArcEdge> connectedArcEdges = new HashSet<ArcEdge>(simpleActivityNode.getConnectedArcEdges());
        for (ArcEdge arcEdge : connectedArcEdges) {
            if (arcEdge instanceof Arc) {
                deleteAllArcEdges.add(new DeleteArcCommand((Arc) arcEdge));
            } else if (arcEdge instanceof ReferenceArc) {
                deleteAllArcEdges.add(new DeleteReferenceArcCommand((ReferenceArc) arcEdge));
            } else {
                throw new RuntimeException("arcEdge not instanceof Arc neither ReferenceArc");
            }
        }
    }

    public void execute() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.execute();
        }
        if(simpleActivityNode == PetriNet.getNewBegin()){
            PetriNet.setOldBegin(PetriNet.getNewBegin());
            PetriNet.setNewBegin(null);
        }
        simpleActivityNode.getParentCompositeActivity().removeElement(simpleActivityNode);
    }

    public void undo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.undo();
        }
        if(simpleActivityNode == PetriNet.getOldBegin()){
            PetriNet.setNewBegin(PetriNet.getOldBegin());
        }
        simpleActivityNode.getParentCompositeActivity().addElement(simpleActivityNode);
    }

    public void redo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.redo();
        }
        simpleActivityNode.getParentCompositeActivity().removeElement(simpleActivityNode);
    }

    @Override
    public String toString() {
        return "Delete simple activity node";
    }

}
