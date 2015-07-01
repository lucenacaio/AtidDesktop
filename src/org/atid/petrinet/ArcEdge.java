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
package org.atid.petrinet;

import java.awt.Graphics;
import java.awt.Point;
import org.atid.util.GraphicsTools;
import org.atid.util.GraphicsTools.HorizontalAlignment;
import org.atid.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class ArcEdge extends Edge implements Cloneable {

    public ArcEdge() {
    }

     public ArcEdge(SimpleActivityNode simpleActivityNode, RepositoryNode repositoryNode, boolean repositoryToSimpleActivity) {
        if (repositoryToSimpleActivity) {
            setSource(repositoryNode);
            setDestination(simpleActivityNode);
        } else {
            setSource(simpleActivityNode);
            setDestination(repositoryNode);
        }
    }
    
    public ArcEdge(SimpleActivityNode simpleActivityNode, CompositeActivity compositeActivity, boolean simpleActivityToTransition) {
        if (simpleActivityToTransition) {
            setSource(simpleActivityNode);
            setDestination(compositeActivity);
        } else {
            setSource(compositeActivity);
            setDestination(simpleActivityNode);
        }
    }
    
    public ArcEdge(CompositeActivity compositeActivity, Transition transition, boolean compositeActivityToTransition) {
        if (compositeActivityToTransition) {
            setSource(compositeActivity);
            setDestination(transition);
        } else {
            setSource(transition);
            setDestination(compositeActivity);
        }
    }

    public ArcEdge(CompositeActivityNode compositeActivityNode, Transition transition, boolean compositeActivityToTransition) {
        if (compositeActivityToTransition) {
            setSource(compositeActivityNode);
            setDestination(transition);
        } else {
            setSource(transition);
            setDestination(compositeActivityNode);
        }
    }

    public ArcEdge(SimpleActivity simpleActivity, Transition transition, boolean simpleActivityToTransition) {
        if (simpleActivityToTransition) {
            setSource(simpleActivity);
            setDestination(transition);
        } else {
            setSource(transition);
            setDestination(simpleActivity);
        }
    }

    public ArcEdge(SimpleActivityNode simpleActivityNode, Transition transition, boolean simpleActivityToTransition) {
        if (simpleActivityToTransition) {
            setSource(simpleActivityNode);
            setDestination(transition);
        } else {
            setSource(transition);
            setDestination(simpleActivityNode);
        }
    }

    public ArcEdge(EventNode eventNode, Transition transition, boolean eventToTransition) {
        if (eventToTransition) {
            setSource(eventNode);
            setDestination(transition);
        }
    }
    
    public ArcEdge(Transition transition, SimpleActivityNode simpleActivityNode, boolean transitionToSimpleActivity) {
        setSource(transition);
        setDestination(simpleActivityNode);
    }
    
    public ArcEdge(Transition transition, CompositeActivityNode compositeActivityNode, boolean transitionToCompositeActivitity) {
        setSource(transition);
        setDestination(compositeActivityNode);
    }
    
    public ArcEdge(RepositoryNode repositoryNode,  SimpleActivityNode simpleActivityNode, boolean repositoryToSimpleActivity) {
        setSource(repositoryNode);
        setDestination(simpleActivityNode);
    }

    public SimpleActivityNode getSimpleActivityNode() {
        return isSimpleActivityToTransition() ? (SimpleActivityNode) getSource() : (SimpleActivityNode) getDestination();
    }
    
    public RepositoryNode getRepositoryNode(){
        if(this.isRepositoryToSimpleActivity()){
            return isRepositoryToSimpleActivity() ? (RepositoryNode) getSource() : (RepositoryNode) getDestination();
        }
        return null;
    }

    public CompositeActivity getCompositeActivityNode() {
        return isCompositeActivityToTransition() ? (CompositeActivity) getSource() : (CompositeActivity) getDestination();
    }

    public EventNode getEventNode(){
       if(isEventToTransition()){ 
           return (EventNode) getSource();
       }
        return null;
    }
    
    public void setSimpleActivityNode(SimpleActivityNode simpleActivityNode) {
        if (isSimpleActivityToTransition()) {
            setSource(simpleActivityNode);
        } else {
            setDestination(simpleActivityNode);
        }
    }

    public TransitionNode getTransitionNode() {
        if(this.eventToTransition()){
            return (TransitionNode) getDestination();
        }else{
            return isSimpleActivityToTransition() ? (TransitionNode) getDestination() : (TransitionNode) getSource();
        }
    }
    


    public void setTransitionNode(TransitionNode transition) {
        if (isSimpleActivityToTransition()) {
            setDestination(transition);
        } else {
            setSource(transition);
        }
    }

    public boolean isSimpleActivityToTransition() {
        return (getSource() instanceof SimpleActivityNode);
    }
    
    public boolean eventToTransition(){
        return (getSource() instanceof EventNode);
    }
    
    public boolean isRepositoryToSimpleActivity(){
        return (getSource() instanceof RepositoryNode); 
    }
 
    public boolean isSimpleActivityToRepository(){
        return (getDestination() instanceof RepositoryNode); 
    }
    
    public boolean isCompositeActivityToTransition() {
        return (getSource() instanceof CompositeActivityNode);
    }
    
    public boolean isEventToTransition(){
        return (getDestination()instanceof TransitionNode); 
    }

    public void setSimpleActivityToTransition(boolean simpleActivityToTransition) {
        if (isSimpleActivityToTransition() != simpleActivityToTransition) {
            reverseBreakPoints();
        }
        if (simpleActivityToTransition && getSource() instanceof TransitionNode && getDestination() instanceof SimpleActivityNode) {
            TransitionNode transitionNode = (TransitionNode) getSource();
            SimpleActivityNode simpleActivityNode = (SimpleActivityNode) getDestination();
            setSource(simpleActivityNode);
            setDestination(transitionNode);
        }
        if (!simpleActivityToTransition && getSource() instanceof SimpleActivityNode && getDestination() instanceof TransitionNode) {
            SimpleActivityNode simpleActivityNode = (SimpleActivityNode) getSource();
            TransitionNode transitionNode = (TransitionNode) getDestination();
            setSource(transitionNode);
            setDestination(simpleActivityNode);
        }
    }

    protected void drawArrow(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawArrowDouble(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        /*int dx =lastBreakPoint.x - arrowTip.x;
         int dy =lastBreakPoint.y - arrowTip.y;
         int px = 8;
         int py = (int) ((dy/dx) * px);
         GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);*/
        GraphicsTools.drawArrowDouble(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawCircle(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        GraphicsTools.drawCircle(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawMultiplicityLabel(Graphics g, Point arrowTip, int multiplicity) {
        Point labelPoint = getLabelPoint(arrowTip);
        GraphicsTools.drawString(g, Integer.toString(multiplicity), labelPoint.x, labelPoint.y, HorizontalAlignment.center, VerticalAlignment.bottom);
    }

    @Override
    public ArcEdge getClone() {
        ArcEdge arcEdge = (ArcEdge) super.getClone();
        arcEdge.setSource(this.getSource());
        arcEdge.setDestination(this.getDestination());
        arcEdge.setBreakPoints(this.getBreakPointsCopy());
        return arcEdge;
    }
    
    public String getArcType() {
        if(this.getSource() instanceof SimpleActivityNode && this.getDestination() instanceof TransitionNode){
            return "simpleActivityToTransition";
        }else if(this.getSource() instanceof TransitionNode && this.getDestination() instanceof SimpleActivityNode){
            return "transitionToSimpleActivity";
        }else if(this.getSource() instanceof CompositeActivityNode && this.getDestination() instanceof TransitionNode){
            return "compositeActivityToTransition";
        }else if(this.getSource() instanceof TransitionNode && this.getDestination() instanceof CompositeActivityNode){
            return "transitionToCompositeActivity";
        }else if(this.getSource() instanceof EventNode && this.getDestination() instanceof TransitionNode){
            return "eventToTransition";
        }else if(this.getSource() instanceof RepositoryNode && this.getDestination() instanceof SimpleActivityNode){
            return "repositoryToSimpleActivity";
        }else if(this.getSource() instanceof SimpleActivityNode && this.getDestination() instanceof RepositoryNode){
            return "simpleActivityToRepository";
        }else{
            return null;
        }
    }
    
    public SimpleActivityNode getSimpleActivityNode(String arcType) {
        if(arcType.equals("simpleActivityToTransition")){
            return (SimpleActivityNode) getSource();
        }else if(arcType.equals("transitionToSimpleActivity")){
            return (SimpleActivityNode) getDestination();
        }else if(arcType.equals("simpleActivityToRepository")){
            return (SimpleActivityNode) getSource();
        }else if(arcType.equals("repositoryToSimpleActivity")){
            return (SimpleActivityNode) getDestination();
        }else{
            return null;
        }      
    }
    
    public CompositeActivity getCompositeActivityNode(String arcType) {
        if(arcType.equals("compositeActivityToTransition")){
            return (CompositeActivity) getSource();
        }else if(arcType.equals("transitionToCompositeActivity")){
            return (CompositeActivity) getDestination();
        }else{
            return null;
        }     
    }
    
    public RepositoryNode getRepositoryNode(String arcType){
        if(arcType.equals("repositoryToSimpleActivity")){
            return (RepositoryNode) getSource();
        }else if(arcType.equals("simpleActivityToRepository")){
            return (RepositoryNode) getDestination();
        }else{
            return null;
        }      
    }
    
    public TransitionNode getTransitionNode(String arcType) {
        if(arcType.equals("transitionToCompositeActivity")){
            return (TransitionNode) getSource();
        }else if(arcType.equals("compositeActivityToTransition")){
            return (TransitionNode) getDestination();
        }else if(arcType.equals("transitionToSimpleActivity")){
            return (TransitionNode) getSource();
        }else if(arcType.equals("simpleActivityToTransition")){
            return (TransitionNode) getDestination();
        }else if(arcType.equals("eventToTransition")){
            return (TransitionNode) getDestination();
        }else{
            return null;
        }     
    }
    
    public EventNode getEventNode(String arcType){
        if(arcType.equals("eventToTransition")){
            return (EventNode) getSource();
        }else{
            return null;
        }   
    }
}
