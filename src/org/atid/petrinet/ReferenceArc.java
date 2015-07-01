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

import java.awt.Graphics;
import java.awt.Point;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReferenceArc extends ArcEdge {

    public ReferenceArc(SimpleActivityNode simpleActivityNode, CompositeActivity compositeActivity) {
        super(simpleActivityNode, compositeActivity, true); //true or false - it is the same. TODO: update it is not the same because of breakpoints order from source to destination node
    }

    public CompositeActivity getCompositeActivity() {
        return (CompositeActivity) getCompositeActivityNode();
    }

    public ReferenceSimpleActivity getReferenceSimpleActivity() {
        for (Element element : getCompositeActivity().getElements()) {
            if (element instanceof ReferenceSimpleActivity) {
                ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) element;
                if (referenceSimpleActivity.getConnectedSimpleActivityNode() == getSimpleActivityNode()) {
                    return referenceSimpleActivity;
                }
            }
        }
        throw new RuntimeException("ReferenceArc: missing ReferenceSimpleActivity");
    }

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        ReferenceSimpleActivity referenceSimpleActivity = getReferenceSimpleActivity();
        if (referenceSimpleActivity.getConnectedTransitionNodes().size() == 1) {
            g.setColor(color);
            GraphicsTools.setDashedStroke(g);
            drawSegmentedLine(g);

            for (Arc arc : referenceSimpleActivity.getConnectedArcs()) { //TODO: also referenceArcs
                setSimpleActivityToTransition(arc.isSimpleActivityToTransition());
                Point arrowTip = computeArrowTipPoint();
                drawArrow(g, arrowTip);

                if (referenceSimpleActivity.getConnectedArcEdges().size() > 1 || arc.getMultiplicity() > 1) {
                    drawMultiplicityLabel(g, arrowTip, arc.getMultiplicity());
                }
            }
            GraphicsTools.setDefaultStroke(g);
        } else if (referenceSimpleActivity.getConnectedTransitionNodes().isEmpty()) {
            GraphicsTools.setDottedStroke(g);
            drawSegmentedLine(g);
            GraphicsTools.setDefaultStroke(g);
        } else {
            GraphicsTools.setDashedStroke(g);
            drawSegmentedLine(g);
            GraphicsTools.setDefaultStroke(g);
        }
    }

}
