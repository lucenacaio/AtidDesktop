/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.editor.commands;

import java.util.HashSet;
import java.util.Set;
import org.atid.petrinet.Arc;
import org.atid.petrinet.ArcEdge;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.ReferenceArc;
import org.atid.util.Command;


public class DeleteEventNodeCommand implements Command{

    
    private EventNode eventNode;
    private Set<Command> deleteAllArcEdges = new HashSet<Command>();

    public DeleteEventNodeCommand(EventNode eventNode) {
        this.eventNode = eventNode;
        Set<ArcEdge> connectedArcs = new HashSet<ArcEdge>(eventNode.getConnectedArcEdges());
        for (ArcEdge arcEdge : connectedArcs) { //TODO: create new class DeleteArcEdgeCommand (use also DeleteSimpleActivityNodeCommand)
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
        eventNode.getParentCompositeActivity().removeElement(eventNode);
    }

    public void undo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.undo();
        }
        eventNode.getParentCompositeActivity().addElement(eventNode);
    }

    public void redo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.redo();
        }
        eventNode.getParentCompositeActivity().removeElement(eventNode);
    }

    @Override
    public String toString() {
        return "Delete transition node";
    }
}


