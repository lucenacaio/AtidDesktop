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
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReferenceSimpleActivity extends SimpleActivityNode {

    private SimpleActivityNode connectedSimpleActivityNode;

    public ReferenceSimpleActivity(SimpleActivityNode connectedSimpleActivityNode) {
        this.connectedSimpleActivityNode = connectedSimpleActivityNode;
    }

    public ReferenceArc getReferenceArc() {
        for (Element element : getParentCompositeActivity().getParentCompositeActivity().getElements()) {
            if (element instanceof ReferenceArc) {
                ReferenceArc referenceArc = (ReferenceArc) element;
                if (referenceArc.getSimpleActivityNode() == connectedSimpleActivityNode
                        && referenceArc.getCompositeActivityNode() == getParentCompositeActivity()) {
                    return referenceArc;
                }
            }
        }
        throw new RuntimeException("ReferenceSimpleActivity: missing ReferenceSimpleActivity");
    }

    public SimpleActivityNode getConnectedSimpleActivityNode() {
        return connectedSimpleActivityNode;
    }

    public SimpleActivity getConnectedSimpleActivity() {
        SimpleActivityNode connSimpleActivityNode = connectedSimpleActivityNode;
        if (connSimpleActivityNode == null) {
            return null;
        }
        while (connSimpleActivityNode instanceof ReferenceSimpleActivity && !(connSimpleActivityNode instanceof SimpleActivity)) {
            ReferenceSimpleActivity connectedReferenceSimpleActivity = (ReferenceSimpleActivity) connSimpleActivityNode;
            connSimpleActivityNode = connectedReferenceSimpleActivity.getConnectedSimpleActivityNode();
        }
        return (SimpleActivity) connSimpleActivityNode;
    }

    public void setConnectedSimpleActivity(SimpleActivityNode simpleActivityNode) {
        connectedSimpleActivityNode = simpleActivityNode;
    }

    /**
     * This call is redirected to the connected SimpleActivityNode. Returns "" if
 connected SimpleActivityNode is null.
     */
    @Override
    public String getLabel() {
        if (connectedSimpleActivityNode == null) {
            return "";
        }
        return connectedSimpleActivityNode.getLabel();
    }

    /**
     * This call is redirected to the connected SimpleActivityNode.
     */
    @Override
    public void setLabel(String label) {
        connectedSimpleActivityNode.setLabel(label);
    }

    /**
     * This call is redirected to the connected SimpleActivityNode. Returns false if
 connected SimpleActivityNode is null.
     */
    @Override
    public boolean isStatic() {
        if (connectedSimpleActivityNode == null) {
            return false;
        }
        return connectedSimpleActivityNode.isStatic();
    }

    /**
     * This call is redirected to the connected SimpleActivityNode.
     */
    @Override
    public void setStatic(boolean isStatic) {
        connectedSimpleActivityNode.setStatic(isStatic);
    }

    @Override
    protected void drawSimpleActivityBorder(Graphics g) {
        GraphicsTools.setDashedStroke(g);
        super.drawSimpleActivityBorder(g);
        GraphicsTools.setDefaultStroke(g);
    }

}
