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
import org.atid.petrinet.CompositeActivityNode;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.ReferenceArc;
import org.atid.util.Command;


public class DeleteCompositeActivityNodeCommand implements Command{

    
    private CompositeActivityNode compositeActivityNode;
    private Set<Command> deleteAllArcEdges = new HashSet<Command>();

    public DeleteCompositeActivityNodeCommand(EventNode eventNode) {
        this.compositeActivityNode = compositeActivityNode;
        Set<ArcEdge> connectedArcs = new HashSet<ArcEdge>(compositeActivityNode.getConnectedArcEdges());
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
        compositeActivityNode.getParentCompositeActivity().removeElement(compositeActivityNode);
    }

    public void undo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.undo();
        }
        compositeActivityNode.getParentCompositeActivity().addElement(compositeActivityNode);
    }

    public void redo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.redo();
        }
        compositeActivityNode.getParentCompositeActivity().removeElement(compositeActivityNode);
    }

    @Override
    public String toString() {
        return "Delete transition node";
    }
}



