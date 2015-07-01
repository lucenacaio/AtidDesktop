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
package org.atid.petrinet;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import org.atid.util.GraphicsTools;
import org.atid.util.GraphicsTools.HorizontalAlignment;
import org.atid.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class SimpleActivityNode extends Node implements Cloneable {

    public Arc getConnectedArc(Transition transition, boolean simpleActivityToTransition) {
        for (Arc arc : getConnectedArcs()) { // TODO: !!! what if the arc is hidden behind ReferenceArc inside Composite Activity ?
            if (arc.getTransition() == transition && arc.isSimpleActivityToTransition() == simpleActivityToTransition) {
                return arc;
            }
        }
        return null;
    }

    public Set<ArcEdge> getConnectedArcEdges(TransitionNode transitionNode) {
        Set<ArcEdge> connectedArcEdgesToTransitionNode = new HashSet<ArcEdge>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            if (arc.getTransitionNode() == transitionNode) {
                connectedArcEdgesToTransitionNode.add(arc);
            }
        }
        return connectedArcEdgesToTransitionNode;
    }

    public Set<Arc> getConnectedArcs(Transition transition) {
        Set<Arc> connectedArcsToTransition = new HashSet<Arc>();
        for (Arc arc : getConnectedArcs()) {
            if (arc.getTransitionNode() == transition) {
                connectedArcsToTransition.add(arc);
            }
        }
        return connectedArcsToTransition;
    }

    abstract public boolean isStatic();

    abstract public void setStatic(boolean isStatic);

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        if (isStatic()) {
            drawStaticShadow(g);
        }
        drawSimpleActivityBackground(g);
        drawSimpleActivityBorder(g);
        drawLabel(g);
        drawTokens(g, drawingOptions.getMarking());
    }

    protected void drawStaticShadow(Graphics g) {
        g.setColor(color);
        final int phase = 4;
        g.fillOval(getStart().x + phase, getStart().y + phase, getWidth() - 1, getHeight() - 1);
    }

    protected void drawSimpleActivityBackground(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(getStart().x, getStart().y, getWidth(), getHeight());
    }

    protected void drawSimpleActivityBorder(Graphics g) {
        g.setColor(color);
        g.drawOval(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
        g.drawOval(getStart().x + 7, getStart().y + 7, getWidth() - 15, getHeight() - 15);
    }

    protected void drawTokens(Graphics g, Marking marking) {
        g.setColor(color);
        int x = getCenter().x;
        int y = getCenter().y;
        int tokenSpacing = getWidth() / 5;
        if (marking.getTokens(this) == 1) {
            drawTokenAsDot(g, x, y);
        } else if (marking.getTokens(this) == 2) {
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
        } else if (marking.getTokens(this) == 3) {
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
        } else if (marking.getTokens(this) == 4) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 5) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 6) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 7) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 8) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y - tokenSpacing);
            drawTokenAsDot(g, x, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 9) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y - tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) > 9) {
            GraphicsTools.drawString(g, Integer.toString(marking.getTokens(this)), x, y, HorizontalAlignment.center, VerticalAlignment.center);
        }
    }

    private void drawTokenAsDot(Graphics g, int x, int y) {
        final int tokenSize = getWidth() / 6;
        g.fillOval(x - tokenSize / 2, y - tokenSize / 2, tokenSize, tokenSize);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        // Check whether (x,y) is inside this oval, using the
        // mathematical equation of an ellipse.
        double rx = getWidth() / 2.0;   // horizontal radius of ellipse
        double ry = getHeight() / 2.0;  // vertical radius of ellipse 
        double cx = getStart().x + rx;   // x-coord of center of ellipse
        double cy = getStart().y + ry;    // y-coord of center of ellipse
        if ((ry * (x - cx)) * (ry * (x - cx)) + (rx * (y - cy)) * (rx * (y - cy)) <= rx * rx * ry * ry) {
            return true;
        } else {
            return false;
        }
    }

    public Set<TransitionNode> getConnectedTransitionNodes() {
        Set<TransitionNode> connectedTransitionNodes = new HashSet<TransitionNode>();
        for (ArcEdge arcEdge : getConnectedArcEdges()) {
            connectedTransitionNodes.add(arcEdge.getTransitionNode());
        }
        return connectedTransitionNodes;
    }

    /**
     * if referenceSimpleActivity.getConnectedSimpleActivity() == null then it
     * returns null too.
     */
    public SimpleActivityNode getSimpleActivity() {
        SimpleActivityNode simpleActivity;
        if (this instanceof ReferenceSimpleActivity) {
            ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) this;
            simpleActivity = referenceSimpleActivity.getConnectedSimpleActivity();
        } else if (this instanceof SimpleActivityNode) {
            simpleActivity = (SimpleActivityNode) this;
        } else {
            throw new RuntimeException("SimpleActivityNode which is not ReferenceSimpleActivity neither Simple Activity");
        }
        return simpleActivity;
    }

    public Set<Transition> getConnectedTransitionsRecursively() {
        Set<Transition> connectedTransitions = new HashSet<Transition>();

        for (Arc arc : getConnectedArcs()) {
            connectedTransitions.add(arc.getTransition());
        }
        return connectedTransitions;
    }

    public Set<SimpleActivityNode> getConnectedSimpleActivityNodes() {
        Set<SimpleActivityNode> connectedSimpleActivityNodes = new HashSet<SimpleActivityNode>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            connectedSimpleActivityNodes.add(arc.getSimpleActivityNode());
        }
        return connectedSimpleActivityNodes;
    }
}
