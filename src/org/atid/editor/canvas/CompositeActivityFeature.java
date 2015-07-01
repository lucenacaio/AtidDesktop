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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.petrinet.CompositeActivity;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CompositeActivityFeature implements Feature {

    private Canvas canvas;

    public CompositeActivityFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        boolean doubleclick = event.getClickCount() == 2;
        if (doubleclick) {
            if (Atid.getRoot().getClickedElement() instanceof CompositeActivity) {
                Atid.getRoot().openCompositeActivity();
            } else if (Atid.getRoot().getClickedElement() == null) {
                Atid.getRoot().closeCompositeActivity();
            }
        }
    }

    public void drawForeground(Graphics g) {
        if (!Atid.getRoot().getDocument().petriNet.isCurrentCompositeActivityRoot()) {
            StringBuilder compositeActivityPath = new StringBuilder("Composite Activity: ");
            for (CompositeActivity compositeActivity : Atid.getRoot().getDocument().petriNet.getOpenedCompositeActivitys()) {
                if (compositeActivity != Atid.getRoot().getDocument().petriNet.getRootCompositeActivity()) {
                    compositeActivityPath.append(compositeActivity.getLabel());
                    if (compositeActivity != Atid.getRoot().getDocument().petriNet.getCurrentCompositeActivity()) {
                        compositeActivityPath.append(" > ");
                    }
                }
            }
            g.setColor(Color.darkGray);
            g.translate(-canvas.getTranslationX(), -canvas.getTranslationY());
            g.drawString(compositeActivityPath.toString(), 2, 2 + g.getFontMetrics().getAscent());
            g.translate(canvas.getTranslationX(), canvas.getTranslationY());
        }
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
