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

import java.util.HashSet;
import java.util.Set;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.PetriNet;
import org.atid.util.CollectionTools;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceCompositeActivitysLooseMethodCommand implements Command {

    private Set<Command> replaceCompositeActivitys = new HashSet<Command>();

    public ReplaceCompositeActivitysLooseMethodCommand(Set<CompositeActivity> compositeActivitys, CompositeActivity storedCompositeActivity, PetriNet petriNet) {
        for (CompositeActivity compositeActivity : compositeActivitys) {
            replaceCompositeActivitys.add(new ReplaceCompositeActivityLooseMethodCommand(compositeActivity, storedCompositeActivity, petriNet));
        }
    }

    public void execute() {
        for (Command command : replaceCompositeActivitys) {
            command.execute();
        }
    }

    public void undo() {
        for (Command command : replaceCompositeActivitys) {
            command.undo();
        }
    }

    public void redo() {
        for (Command command : replaceCompositeActivitys) {
            command.redo();
        }
    }

    @Override
    public String toString() {
        if (replaceCompositeActivitys.size() == 1) {
            return CollectionTools.getFirstElement(replaceCompositeActivitys).toString();
        }
        return "Replace Composite Activitys";
    }
}
