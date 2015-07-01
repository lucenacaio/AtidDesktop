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

import org.atid.petrinet.Marking;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.util.Command;

/**
 * Set tokens to clicked simple activity node
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetTokensCommand implements Command {

    private SimpleActivityNode simpleActivityNode;
    private int newValue;
    private Marking marking;

    public SetTokensCommand(SimpleActivityNode simpleActivityNode, int tokens, Marking marking) {
        this.simpleActivityNode = simpleActivityNode;
        this.newValue = tokens;
        this.marking = marking;
    }

    private int oldValue;

    public void execute() {
        oldValue = marking.getTokens(simpleActivityNode);
        marking.setTokens(simpleActivityNode, newValue);
    }

    public void undo() {
        marking.setTokens(simpleActivityNode, oldValue);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set tokens";
    }
}
