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
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.editor.commands.AddCompositeActivityCommand;
import org.atid.editor.commands.AddEventCommand;
import org.atid.editor.commands.AddRepositoryCommand;
import org.atid.editor.commands.AddSimpleActivityCommand;
import org.atid.editor.commands.AddTransitionCommand;
import org.atid.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SimpleActivityTransitionCompositeActivityEventRepositoryMakerFeature implements Feature {

    private Canvas canvas;

    public SimpleActivityTransitionCompositeActivityEventRepositoryMakerFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1) {
            if (Atid.getRoot().getClickedElement() == null) {
                if (Atid.getRoot().isSelectedTool_SimpleActivity()) {
                    Atid.getRoot().getSelection().clear();
                    Atid.getRoot().getUndoManager().executeCommand(new AddSimpleActivityCommand(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity(), x, y, Atid.getRoot().getDocument().petriNet));
                    Atid.getRoot().setClickedElement(CollectionTools.getLastElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                } else if (Atid.getRoot().isSelectedTool_Transition()) {
                    Atid.getRoot().getSelection().clear();
                    Atid.getRoot().getUndoManager().executeCommand(new AddTransitionCommand(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity(), x, y, Atid.getRoot().getDocument().petriNet));
                    Atid.getRoot().setClickedElement(CollectionTools.getLastElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                } else if (Atid.getRoot().isSelectedTool_CompositeActivity()){
                    Atid.getRoot().getSelection().clear();
                    Atid.getRoot().getUndoManager().executeCommand(new AddCompositeActivityCommand(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity(), x, y, Atid.getRoot().getDocument().petriNet));
                    Atid.getRoot().setClickedElement(CollectionTools.getLastElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                }   else if(Atid.getRoot().isSelectTool_Event()){
                    Atid.getRoot().getSelection().clear();
                    Atid.getRoot().getUndoManager().executeCommand(new AddEventCommand(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity(), x, y, Atid.getRoot().getDocument().petriNet));
                    Atid.getRoot().setClickedElement(CollectionTools.getLastElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                }   else if(Atid.getRoot().isSelectedTool_Repository()){
                    Atid.getRoot().getSelection().clear();
                    Atid.getRoot().getUndoManager().executeCommand(new AddRepositoryCommand(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity(), x, y, Atid.getRoot().getDocument().petriNet));
                    Atid.getRoot().setClickedElement(CollectionTools.getLastElement(Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()));
                }
                
            }
        }

    }

    public void drawForeground(Graphics g) {
    }

    public void drawBackground(Graphics g) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void setHoverEffects(int x, int y) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
