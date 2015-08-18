/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.petrinet;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author caiolucena
 */
public class EventNode extends Node implements Cloneable {

    public Set<SimpleActivityNode> getConnectedSimpleActivityNodes() {
         Set<SimpleActivityNode> connectedSimpleActivityNodes = new HashSet<SimpleActivityNode>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            connectedSimpleActivityNodes.add(arc.getSimpleActivityNode());
        }
        return connectedSimpleActivityNodes;
    }

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
