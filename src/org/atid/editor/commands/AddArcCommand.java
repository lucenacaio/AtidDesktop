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

import org.atid.petrinet.Arc;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.RepositoryNode;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.Transition;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddArcCommand implements Command {

    private CompositeActivity parentCompositeActivity;
    private SimpleActivityNode simpleActivityNode;
    private EventNode eventNode;
    private RepositoryNode repositoryNode;
    private Transition transition;
    private boolean eventToTransition;
    private boolean simpleActivityToTransition;
    private boolean repositoryToSimpleActivity;
    private Arc createdArc;

    public AddArcCommand(SimpleActivityNode simpleActivityNode, Transition transition, boolean simpleActivityToTransition) {
        this.parentCompositeActivity = simpleActivityNode.getParentCompositeActivity();
        this.simpleActivityNode = simpleActivityNode;
        this.transition = transition;
        this.simpleActivityToTransition = simpleActivityToTransition;
    }
    
    public AddArcCommand(SimpleActivityNode simpleActivityNode, RepositoryNode repositoryNode, boolean repositoryToSimpleActivity){
        this.parentCompositeActivity = simpleActivityNode.getParentCompositeActivity();
        this.simpleActivityNode = simpleActivityNode;
        this.repositoryNode = repositoryNode;
        this.repositoryToSimpleActivity = repositoryToSimpleActivity;
    }
    
    public AddArcCommand(EventNode eventNode, Transition transition, boolean  eventToTransition){
        this.eventNode = eventNode;
        this.parentCompositeActivity = eventNode.getParentCompositeActivity();
        this.transition = transition;
        this.eventToTransition = eventToTransition;
    }

    public void execute() {
        if(this.eventToTransition){
            createdArc = new Arc(eventNode, transition, eventToTransition);
            parentCompositeActivity.addElement(createdArc);
        }else if(this.repositoryToSimpleActivity || !this.repositoryToSimpleActivity && repositoryNode != null){
            createdArc = new Arc(simpleActivityNode, repositoryNode, repositoryToSimpleActivity);
            parentCompositeActivity.addElement(createdArc);
        }
        else{
            createdArc = new Arc(simpleActivityNode, transition, simpleActivityToTransition);
            parentCompositeActivity.addElement(createdArc);
        }
    }

    public void undo() {
        new DeleteElementCommand(createdArc).execute();
    }

    public void redo() {
        parentCompositeActivity.addElement(createdArc);
    }

    @Override
    public String toString() {
        return "Add arc";
    }

    public Arc getCreatedArc() { //TODO: check usage
        return createdArc;
    }
}
