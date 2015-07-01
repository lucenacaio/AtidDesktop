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
import org.atid.petrinet.ReferenceArc;
import org.atid.petrinet.RepositoryNode;
import org.atid.util.Command;


public class DeleteRepositoryNodeCommand implements Command{

    
    private RepositoryNode repositoryNode;
    private Set<Command> deleteAllArcEdges = new HashSet<Command>();

    public DeleteRepositoryNodeCommand(RepositoryNode repositoryNode) {
        this.repositoryNode = repositoryNode;
        Set<ArcEdge> connectedArcs = new HashSet<ArcEdge>(repositoryNode.getConnectedArcEdges());
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
        repositoryNode.getParentCompositeActivity().removeElement(repositoryNode);
    }

    public void undo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.undo();
        }
        repositoryNode.getParentCompositeActivity().addElement(repositoryNode);
    }

    public void redo() {
        for (Command deleteArc : deleteAllArcEdges) {
            deleteArc.redo();
        }
        repositoryNode.getParentCompositeActivity().removeElement(repositoryNode);
    }

    @Override
    public String toString() {
        return "Delete transition node";
    }
}



