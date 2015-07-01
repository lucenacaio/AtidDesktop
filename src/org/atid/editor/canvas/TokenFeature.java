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

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.editor.commands.AddTokenCommand;
import org.atid.editor.commands.FireTransitionCommand;
import org.atid.editor.commands.RemoveTokenCommand;
import org.atid.petrinet.CompositeActivityNode;
import org.atid.petrinet.Element;
import org.atid.petrinet.Marking;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.Transition;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class TokenFeature implements Feature {

    private Canvas canvas;

    private Cursor tokenCursor;
    private Cursor fireCursor;

    TokenFeature(Canvas canvas) {
        this.canvas = canvas;
        tokenCursor = GraphicsTools.getCursor("atid/canvas/token.gif", new Point(16, 0));
        fireCursor = GraphicsTools.getCursor("atid/canvas/fire.gif", new Point(16, 0));
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();
        Marking initialMarking = Atid.getRoot().getCurrentMarking();

        if (Atid.getRoot().getClickedElement() != null
                && Atid.getRoot().isSelectedTool_Token()) {
            Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);

            if (targetElement instanceof SimpleActivityNode) {
                SimpleActivityNode simpleActivityNode = (SimpleActivityNode) targetElement;
                if (mouseButton == MouseEvent.BUTTON1) {
                    Atid.getRoot().getUndoManager().executeCommand(new AddTokenCommand(simpleActivityNode, initialMarking));
                } else if (mouseButton == MouseEvent.BUTTON3) {
                    if (initialMarking.getTokens(simpleActivityNode) > 0) {
                        Atid.getRoot().getUndoManager().executeCommand(new RemoveTokenCommand(simpleActivityNode, initialMarking));
                    }
                }
            } else if (targetElement instanceof Transition) {
                Transition transition = (Transition) targetElement;
                if (mouseButton == MouseEvent.BUTTON1) {
                    if (initialMarking.isEnabled(transition)) {
                        Atid.getRoot().getUndoManager().executeCommand(new FireTransitionCommand(transition, initialMarking));
                    }
                }
            }
        }
    }

    public void setHoverEffects(int x, int y) {
        Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);
        Marking initialMarking = Atid.getRoot().getCurrentMarking();

        if (Atid.getRoot().isSelectedTool_Token()) {
            if (targetElement instanceof SimpleActivityNode) {
                canvas.highlightedElements.add(targetElement);
                targetElement.highlightColor = Colors.pointingColor;
                canvas.repaint();
            } else if(targetElement instanceof CompositeActivityNode){
                canvas.highlightedElements.add(targetElement);
                targetElement.highlightColor = Colors.pointingColor;
                canvas.repaint();
            } else if (targetElement instanceof Transition) {
                if (initialMarking.isEnabled((Transition) targetElement)) {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.permittedColor;
                    canvas.repaint();
                } else {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.disallowedColor;
                    canvas.repaint();
                }
            }
        }
    }

    public void drawForeground(Graphics g) {
        Marking initialMarking = Atid.getRoot().getCurrentMarking();

        if (Atid.getRoot().isSelectedTool_Token()) {
            for (Element element : Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElements()) {
                if (element instanceof Transition) {
                    Transition transition = (Transition) element;
                    if (initialMarking.isEnabled(transition)) {
                        g.setColor(Colors.permittedColor);
                    } else {
                        g.setColor(Colors.disallowedColor);
                    }
                    ((Graphics2D) g).setStroke(new BasicStroke(2f));
                    g.drawRect(transition.getStart().x + 1, transition.getStart().y + 1, transition.getWidth() - 3, transition.getHeight() - 3);
                    ((Graphics2D) g).setStroke(new BasicStroke(1f));
                }
            }
        }
    }

    public void setCursor(int x, int y) {
        Element targetElement = Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity().getElementByXY(x, y);

        if (Atid.getRoot().isSelectedTool_Token()
                && targetElement != null) {
            if (targetElement instanceof SimpleActivityNode) {

                canvas.alternativeCursor = tokenCursor;
            } else if (targetElement instanceof Transition) {
                canvas.alternativeCursor = fireCursor;
            }
        }

    }

    public void drawBackground(Graphics g) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
