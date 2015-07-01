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

import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.ReferenceArc;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddReferenceArcCommand implements Command {

    private CompositeActivity parentCompositeActivity;
    private SimpleActivityNode simpleActivityNode;
    private CompositeActivity nestedCompositeActivity;
    private ReferenceArc createdReferenceArc;
    private ReferenceSimpleActivity referenceSimpleActivity;
    private PetriNet petriNet;

    public AddReferenceArcCommand(SimpleActivityNode parentCompositeActivity, CompositeActivity nestedCompositeActivity, PetriNet petriNet) {
        this.parentCompositeActivity = simpleActivityNode.getParentCompositeActivity();
        this.simpleActivityNode = simpleActivityNode;
        this.nestedCompositeActivity = nestedCompositeActivity;
        this.petriNet = petriNet;
    }

    public void execute() {
        referenceSimpleActivity = new ReferenceSimpleActivity(simpleActivityNode);
        referenceSimpleActivity.setCenter(
                simpleActivityNode.getCenter().x - nestedCompositeActivity.getCenter().x,
                simpleActivityNode.getCenter().y - nestedCompositeActivity.getCenter().y
        );
        petriNet.getNodeSimpleIdGenerator().setUniqueId(referenceSimpleActivity);
        createdReferenceArc = new ReferenceArc(simpleActivityNode, nestedCompositeActivity);
        redo();
    }

    public void undo() {
        new DeleteElementCommand(createdReferenceArc).execute();
    }

    public void redo() {
        nestedCompositeActivity.addElement(referenceSimpleActivity);
        parentCompositeActivity.addElement(createdReferenceArc);
    }

    @Override
    public String toString() {
        return "Add reference arc";
    }

}
