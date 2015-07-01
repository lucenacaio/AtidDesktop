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
package org.atid.editor.canvas;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.atid.editor.Atid;
import org.atid.editor.commands.AddArcCommand;
import org.atid.editor.commands.SetArcMultiplicityCommand;
import org.atid.petrinet.Arc;
import org.atid.petrinet.ArcEdge;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.CompositeActivityNode;
import org.atid.petrinet.Element;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.Node;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.ReferenceArc;
import org.atid.petrinet.RepositoryNode;
import org.atid.petrinet.Transition;
import org.atid.petrinet.TransitionNode;
import org.atid.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class ArcFeature implements Feature {

    private Canvas canvas;
    ArcEdge counterArcEdge;
    ArcEdge arcEdge;
    
    ArcFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    private Element sourceElement = null;
    private Arc connectingArc = null;
    private List<Element> backgroundElements = new ArrayList<Element>();
    private boolean started = false;
    private CompositeActivity currentCompositeActivity;

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && Atid.getRoot().isSelectedTool_Arc()
                && Atid.getRoot().getClickedElement() != null
                && Atid.getRoot().getClickedElement() instanceof Node
                && !started) {
            sourceElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);
            connectingArc = new Arc((Node) sourceElement);
            backgroundElements.add(connectingArc);
            started = true;
            currentCompositeActivity = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity();
        }
    }

    public void mouseDragged(int x, int y) {
        if (Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity() != currentCompositeActivity) {
            cancelDragging();
        }

        Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);

        if (started) {
            if (targetElement != null && (sourceElement instanceof SimpleActivityNode && targetElement instanceof TransitionNode
                    || sourceElement instanceof TransitionNode && targetElement instanceof SimpleActivityNode ||
                    sourceElement instanceof EventNode && targetElement instanceof TransitionNode
                    || sourceElement instanceof SimpleActivityNode && targetElement instanceof RepositoryNode
                    || sourceElement instanceof RepositoryNode && targetElement instanceof SimpleActivityNode
                    )) {
                connectingArc.setEnd(targetElement.getCenter().x, targetElement.getCenter().y);
                connectingArc.setDestination((Node) targetElement);
            } else{
                connectingArc.setEnd(x, y);
                connectingArc.setSource(null);
                connectingArc.setDestination(null);
            }
            Atid.getRoot().repaintCanvas();
        }
    }

    public void mouseMoved(int x, int y) {
        mouseDragged(x, y);
    }

    public void mouseReleased(int x, int y) {
        if (Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity() != currentCompositeActivity) {
            cancelDragging();
        }
        Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);

        if (started) {
            connectingArc.setEnd(x, y);
            if (sourceElement != targetElement) {
               
                if (targetElement != null) {
                    if (sourceElement instanceof SimpleActivityNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof TransitionNode && targetElement instanceof SimpleActivityNode
                            || sourceElement instanceof EventNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof RepositoryNode && targetElement instanceof SimpleActivityNode && !(targetElement instanceof CompositeActivity)
                            || sourceElement instanceof SimpleActivityNode && !(sourceElement instanceof CompositeActivity) && targetElement instanceof RepositoryNode) {
                        SimpleActivityNode simpleActivityNode = null;
                        boolean simpleActivityToTransition = sourceElement instanceof SimpleActivityNode && targetElement instanceof TransitionNode;
                        boolean transitionToSimpleActivity = sourceElement instanceof TransitionNode && targetElement instanceof SimpleActivityNode;
                        boolean eventToTransition = sourceElement instanceof EventNode && targetElement instanceof TransitionNode;
                        boolean repositoryToSimpleActivity = sourceElement instanceof RepositoryNode && targetElement instanceof SimpleActivityNode;
                        TransitionNode transitionNode = null;
                        EventNode eventNode = null;
                        RepositoryNode repositoryNode = null;
                        if (simpleActivityToTransition) {
                            simpleActivityNode = (SimpleActivityNode) sourceElement;
                            transitionNode = (TransitionNode) targetElement;
                        } else if(repositoryToSimpleActivity && !(targetElement instanceof CompositeActivity)){
                            repositoryNode = (RepositoryNode) sourceElement;
                            simpleActivityNode = (SimpleActivityNode) targetElement;
                        } else if(eventToTransition){
                            eventNode = (EventNode) sourceElement;
                            transitionNode = (TransitionNode) targetElement;
                        }else if((!repositoryToSimpleActivity) && !(targetElement instanceof TransitionNode) && !(targetElement instanceof SimpleActivityNode) && !(sourceElement instanceof CompositeActivity)){
                            simpleActivityNode = (SimpleActivityNode) sourceElement;
                            repositoryNode = (RepositoryNode) targetElement;
                        } else if(transitionToSimpleActivity) {
                            transitionNode = (TransitionNode) sourceElement;
                            simpleActivityNode = (SimpleActivityNode) targetElement;
                        }
                        
                        if(eventToTransition){
                            this.arcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(eventNode, transitionNode, eventToTransition);
                            this.counterArcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(eventNode, transitionNode, !eventToTransition);
                        } else if(repositoryToSimpleActivity){
                                this.arcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, repositoryNode, repositoryToSimpleActivity);
                                this.counterArcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, repositoryNode, !repositoryToSimpleActivity);
                        } else if((!repositoryToSimpleActivity) && !(targetElement instanceof TransitionNode) && !(targetElement instanceof SimpleActivityNode)){
                                this.arcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, repositoryNode, repositoryToSimpleActivity);
                                this.counterArcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, repositoryNode, !repositoryToSimpleActivity);
                        } else if(transitionToSimpleActivity){
                            this.arcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, transitionNode, simpleActivityToTransition);
                            this.counterArcEdge = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getArcEdge(simpleActivityNode, transitionNode, !simpleActivityToTransition);
                        }                        
                        if (counterArcEdge instanceof ReferenceArc) {
                            // never attempt make arc in opposite direction of ReferenceArc
                        } else if (arcEdge == null) {
                            // is there is no arc go ahead
                            if (transitionNode instanceof Transition) {
                               if(eventToTransition){
                                   Atid.getRoot().getUndoManager().executeCommand(new AddArcCommand(eventNode, (Transition) transitionNode, eventToTransition));
                               }
                               else if(transitionToSimpleActivity || simpleActivityToTransition){
                                    Atid.getRoot().getUndoManager().executeCommand(new AddArcCommand(simpleActivityNode, (Transition) transitionNode, simpleActivityToTransition));
                               }
                           }else if(repositoryToSimpleActivity || !repositoryToSimpleActivity && repositoryNode != null){
                               if(!transitionToSimpleActivity ){
                                    Atid.getRoot().getUndoManager().executeCommand(new AddArcCommand(simpleActivityNode,(RepositoryNode) repositoryNode, repositoryToSimpleActivity));
                                    repositoryNode.setConnected();
                               }
                           } else {
                                throw new RuntimeException("transitionNode not instanceof Transition neither Composite Activity");
                            }

                            Atid.getRoot().setClickedElement(CollectionTools.getFirstElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                        } else if (!(arcEdge instanceof ReferenceArc)) {
                            Arc arc = (Arc) arcEdge;
                            Atid.getRoot().getUndoManager().executeCommand(new SetArcMultiplicityCommand(arc, arc.getMultiplicity() + 1));
                            Atid.getRoot().setClickedElement(arcEdge);
                        }
                    }
                }
                
                cancelDragging();
            }
        }
    }

    public void setHoverEffects(int x, int y) {
        Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);
        if (Atid.getRoot().isSelectedTool_Arc()) {
            if (started) { // Connecting to something...
                if (targetElement == null) { // Connecting to air
                    canvas.highlightedElements.add(sourceElement);
                    sourceElement.highlightColor = Colors.pointingColor;
                    Atid.getRoot().repaintCanvas();
                } else { // Connecting to solid element
                    if (sourceElement instanceof SimpleActivityNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof TransitionNode && targetElement instanceof SimpleActivityNode 
                            || sourceElement instanceof EventNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof SimpleActivityNode && targetElement instanceof RepositoryNode
                            || sourceElement instanceof RepositoryNode && targetElement instanceof SimpleActivityNode
                            ) {
                        if(sourceElement instanceof RepositoryNode || targetElement instanceof RepositoryNode){
                             RepositoryNode element;
                            if(sourceElement instanceof RepositoryNode){
                                element = (RepositoryNode) sourceElement; 
                            }
                            else{
                                element = (RepositoryNode) targetElement;
                            }
                            if((sourceElement instanceof RepositoryNode && !(targetElement instanceof CompositeActivity)) || (targetElement instanceof RepositoryNode && !(sourceElement instanceof CompositeActivity))){
                                canvas.highlightedElements.add(sourceElement);
                                canvas.highlightedElements.add(targetElement);
                                sourceElement.highlightColor = Colors.connectingColor;
                                targetElement.highlightColor = Colors.connectingColor;
                                Atid.getRoot().repaintCanvas();
                            }else{
                                canvas.highlightedElements.add(sourceElement);
                                canvas.highlightedElements.add(targetElement);
                                sourceElement.highlightColor = Colors.disallowedColor;
                                targetElement.highlightColor = Colors.disallowedColor;
                                Atid.getRoot().repaintCanvas();
                            }

                            }
                        else{
                            canvas.highlightedElements.add(sourceElement);
                            canvas.highlightedElements.add(targetElement);
                            sourceElement.highlightColor = Colors.connectingColor;
                            targetElement.highlightColor = Colors.connectingColor;
                            Atid.getRoot().repaintCanvas();
                        }
                    } else if (sourceElement == targetElement) {
                        canvas.highlightedElements.add(sourceElement);
                        sourceElement.highlightColor = Colors.pointingColor;
                        Atid.getRoot().repaintCanvas();
                    } else if (targetElement instanceof Node) { // Wrong combination
                        canvas.highlightedElements.add(sourceElement);
                        canvas.highlightedElements.add(targetElement);
                        sourceElement.highlightColor = Colors.disallowedColor;
                        targetElement.highlightColor = Colors.disallowedColor;
                        Atid.getRoot().repaintCanvas();
                    }
                }
            } else {
                if (targetElement != null) {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.pointingColor;
                    Atid.getRoot().repaintCanvas();
                }
            }
        }
    }

    public void drawBackground(Graphics g) {
        for (Element element : backgroundElements) {
            element.draw(g, null);
        }
    }

    public void drawForeground(Graphics g) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    private void cancelDragging() { 
        sourceElement = null;
        backgroundElements.remove(connectingArc);
        started = false;
        Atid.getRoot().repaintCanvas();
    }
    
}
