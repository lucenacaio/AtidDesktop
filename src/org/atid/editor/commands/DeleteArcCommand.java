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
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteArcCommand implements Command {

    private Arc arc;
    private boolean isAlreadyDeleted;

    public DeleteArcCommand(Arc arc) {
        this.arc = arc;
    }

    public void execute() {
        isAlreadyDeleted = !arc.getParentCompositeActivity().getElements().contains(arc);
        if (!isAlreadyDeleted) {
            arc.getParentCompositeActivity().removeElement(arc);
        }
    }

    public void undo() {
        if (!isAlreadyDeleted) {
            arc.getParentCompositeActivity().addElement(arc);
        }
    }

    public void redo() {
        isAlreadyDeleted = !arc.getParentCompositeActivity().getElements().contains(arc);
        if (!isAlreadyDeleted) {
            arc.getParentCompositeActivity().removeElement(arc);
        }
    }

    @Override
    public String toString() {
        return "Delete arc";
    }

}
