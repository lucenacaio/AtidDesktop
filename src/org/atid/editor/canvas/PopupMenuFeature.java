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
import javax.swing.JPopupMenu;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.petrinet.ArcEdge;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PopupMenuFeature implements Feature {

    private Canvas canvas;

    public PopupMenuFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        int realX = x + canvas.getTranslationX();
        int realY = y + canvas.getTranslationY();

        if (mouseButton == MouseEvent.BUTTON3) {
            if (Atid.getRoot().getClickedElement() != null
                    && (Atid.getRoot().isSelectedTool_Select()
                    || Atid.getRoot().isSelectedTool_SimpleActivity()
                    || Atid.getRoot().isSelectedTool_Transition()
                    || Atid.getRoot().isSelectedTool_Arc()
                    || Atid.getRoot().isSelectedTool_Token() && !(Atid.getRoot().getClickedElement() instanceof SimpleActivityNode))) {
                if (Atid.getRoot().getClickedElement() instanceof SimpleActivityNode) {
                    showPopup(Atid.getRoot().getSimpleActivityPopup(), realX, realY);
                    if (!Atid.getRoot().getSelection().contains(Atid.getRoot().getClickedElement())) {
                        Atid.getRoot().getSelection().clear();
                    }
                } else if (Atid.getRoot().getClickedElement() instanceof CompositeActivity) {
                    showPopup(Atid.getRoot().getSubnetPopup(), realX, realY);
                    if (!Atid.getRoot().getSelection().contains(Atid.getRoot().getClickedElement())) {
                        Atid.getRoot().getSelection().clear();
                    }
                } else if (Atid.getRoot().getClickedElement() instanceof Transition) {
                    showPopup(Atid.getRoot().getTransitionPopup(), realX, realY);
                    if (!Atid.getRoot().getSelection().contains(Atid.getRoot().getClickedElement())) {
                        Atid.getRoot().getSelection().clear();
                    }
                } else if (Atid.getRoot().getClickedElement() instanceof ArcEdge) {
                    showPopup(Atid.getRoot().getArcEdgePopup(), realX, realY);
                    if (!Atid.getRoot().getSelection().contains(Atid.getRoot().getClickedElement())) {
                        Atid.getRoot().getSelection().clear();
                    }
                }
            }

            if (Atid.getRoot().getClickedElement() == null
                    && Atid.getRoot().isSelectedTool_Select()) {
                showPopup(Atid.getRoot().getCanvasPopup(), realX, realY);
            }
        }
    }

    private void showPopup(JPopupMenu popupMenu, int clickedX, int clickedY) {
        popupMenu.show(canvas, clickedX - 10, clickedY - 2);
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
