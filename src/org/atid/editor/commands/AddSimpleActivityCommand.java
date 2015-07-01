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
import org.atid.petrinet.SimpleActivity;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddSimpleActivityCommand implements Command {

    private CompositeActivity compositeActivity;
    private int x, y;
    private SimpleActivity createdSimpleActivity;
    private PetriNet petriNet;

    public AddSimpleActivityCommand(CompositeActivity compositeActivity, int x, int y, PetriNet petriNet) {
        this.compositeActivity = compositeActivity;
        this.x = x;
        this.y = y;
        this.petriNet = petriNet;
    }

    @Override
    public void execute() {
        createdSimpleActivity = new SimpleActivity();
        createdSimpleActivity.setCenter(x, y);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(createdSimpleActivity);
        petriNet.getNodeLabelGenerator().setLabelToNewlyCreatedNode(createdSimpleActivity);
        compositeActivity.addElement(createdSimpleActivity);
    }

    public void undo() {
        new DeleteElementCommand(createdSimpleActivity).execute();
    }

    public void redo() {
        compositeActivity.addElement(createdSimpleActivity);
    }

    @Override
    public String toString() {
        return "Add Simple Activity";
    }

    public SimpleActivity getCreatedSimpleActivity() {
        return createdSimpleActivity;
    }
}
