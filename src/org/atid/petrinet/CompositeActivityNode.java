/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.petrinet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Atylla
 */
public abstract class CompositeActivityNode extends Node implements Cloneable {
    
      public Arc getConnectedArc(Transition transition, boolean compositeActivityToTransition) {
        for (Arc arc : getConnectedArcs()) { // TODO: !!! what if the arc is hidden behind ReferenceArc inside Composite Activity ?
            if (arc.getTransition() == transition && arc.isCompositeActivityToTransition() == compositeActivityToTransition) {
                return arc;
            }
        }
        return null;
    }

    public Set<ArcEdge> getConnectedArcEdges(TransitionNode transitionNode) {
        Set<ArcEdge> connectedArcEdgesToTransitionNode = new HashSet<ArcEdge>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            if (arc.getTransitionNode() == transitionNode) {
                connectedArcEdgesToTransitionNode.add(arc);
            }
        }
        return connectedArcEdgesToTransitionNode;
    }

    public Set<Arc> getConnectedArcs(Transition transition) {
        Set<Arc> connectedArcsToTransition = new HashSet<Arc>();
        for (Arc arc : getConnectedArcs()) {
            if (arc.getTransitionNode() == transition) {
                connectedArcsToTransition.add(arc);
            }
        }
        return connectedArcsToTransition;
    }

    abstract public boolean isStatic();

    abstract public void setStatic(boolean isStatic);


    public Set<TransitionNode> getConnectedTransitionNodes() {
        Set<TransitionNode> connectedTransitionNodes = new HashSet<TransitionNode>();
        for (ArcEdge arcEdge : getConnectedArcEdges()) {
            connectedTransitionNodes.add(arcEdge.getTransitionNode());
        }
        return connectedTransitionNodes;
    }

    /**
     * if referenceSimpleActivity.getConnectedSimpleActivity() == null then it returns null too.
     */


    public Set<Transition> getConnectedTransitionsRecursively() {
        Set<Transition> connectedTransitions = new HashSet<Transition>();

        for (Arc arc : getConnectedArcs()) {
            connectedTransitions.add(arc.getTransition());
        }


        return connectedTransitions;
    }

        public Set<CompositeActivity> getConnectedCompositeActivityNodes() {
            Set<CompositeActivity> connectedCompositeActivityNodes = new HashSet<CompositeActivity>();
            for (ArcEdge arc : getConnectedArcEdges()) {
                connectedCompositeActivityNodes.add(arc.getCompositeActivityNode());
            }
            return connectedCompositeActivityNodes;
    }

    public void removeElement(CompositeActivityNode compositeActivityNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addElement(CompositeActivityNode compositeActivityNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
