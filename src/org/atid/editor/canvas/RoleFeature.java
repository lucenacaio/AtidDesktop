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
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Role;
import org.atid.petrinet.Transition;
import org.atid.petrinet.TransitionNode;
import org.atid.util.CollectionTools;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RoleFeature implements Feature {

    private Canvas canvas;

    private BufferedImage fullRoleImage, partialRoleImage, mixedRoleImage;

    public RoleFeature(Canvas canvas) {
        this.canvas = canvas;
        fullRoleImage = GraphicsTools.getBufferedImage("atid/canvas/fullrole.gif");
        partialRoleImage = GraphicsTools.getBufferedImage("atid/canvas/partialrole.gif");
        mixedRoleImage = GraphicsTools.getBufferedImage("atid/canvas/mixedrole.gif");
    }

    public void drawForeground(Graphics g) {
                Set<CompositeActivity> partiallyIncluded = new HashSet<CompositeActivity>();
        Set<CompositeActivity> fullyIncluded = null;
        Set<CompositeActivity> mixedIncluded = new HashSet<CompositeActivity>();

        if (fullyIncluded != null) {
            partiallyIncluded.removeAll(fullyIncluded);
            for (CompositeActivity  transition : partiallyIncluded) {
                GraphicsTools.drawImageCentered(g, partialRoleImage, transition.getStart().x + transition.getWidth() / 2, transition.getStart().y + transition.getHeight() / 2);
            }
            for (CompositeActivity transition : fullyIncluded) {
                GraphicsTools.drawImageCentered(g, fullRoleImage, transition.getStart().x + transition.getWidth() / 2, transition.getStart().y + transition.getHeight() / 2);
            }
        }
        for (CompositeActivity compositeActivity : mixedIncluded) {
            GraphicsTools.drawImageCentered(g, mixedRoleImage, compositeActivity.getStart().x + compositeActivity.getWidth() / 2, compositeActivity.getStart().y + compositeActivity.getHeight() / 2);
        }
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void setHoverEffects(int x, int y) {
    }

    public void drawBackground(Graphics g) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
