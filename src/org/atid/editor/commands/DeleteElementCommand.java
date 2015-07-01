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
import org.atid.petrinet.Element;
import org.atid.petrinet.Event;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.Node;
import org.atid.petrinet.NodeLabelGenerator;
import org.atid.petrinet.ReferenceArc;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.Repository;
import org.atid.petrinet.SimpleActivity;
import org.atid.petrinet.TransitionNode;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteElementCommand implements Command {

    private Element element;
    private Command deleteElement;

    public DeleteElementCommand(Element elementToDelete) {
        this.element = elementToDelete;
        if (element instanceof SimpleActivity) {
            SimpleActivity simpleActivity = (SimpleActivity) element;
            deleteElement = new DeleteSimpleActivityNodeCommand(simpleActivity);
        } else if (element instanceof TransitionNode) {
            TransitionNode transition = (TransitionNode) element;
            deleteElement = new DeleteTransitionNodeCommand(transition);
        } else if (element instanceof ReferenceArc) {
            ReferenceArc referenceArc = (ReferenceArc) element;
            deleteElement = new DeleteReferenceArcCommand(referenceArc);
        } else if (element instanceof Arc) {
            Arc arc = (Arc) element;
            deleteElement = new DeleteArcCommand(arc);
        } else if (element instanceof Event){
            EventNode event = (EventNode) element;
            deleteElement = new DeleteEventNodeCommand(event);
        } else if (element instanceof CompositeActivity) {
            CompositeActivity compositeActivity = (CompositeActivity) element;
            deleteElement = new DeleteSimpleActivityNodeCommand(compositeActivity);
        } else{
            Repository repository = (Repository) element;
            deleteElement = new DeleteRepositoryNodeCommand(repository);
        }    
    }

    public void execute() {
        if (deleteElement != null) {
            deleteElement.execute();
        }
    }

    public void undo() {
        if (deleteElement != null) {
            deleteElement.undo();
        }
    }

    public void redo() {
        if (deleteElement != null) {
            deleteElement.redo();
        }
    }

    @Override
    public String toString() {
        return deleteElement.toString();
    }
}
