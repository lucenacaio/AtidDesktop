/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.petrinet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caiolucena
 */
public class Event extends EventNode {

    private int npoints = 3;
 
    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        drawSimpleActivityBackground(g);
        drawEventBorder(g);
        drawLabel(g);
    }

    protected void drawSimpleActivityBackground(Graphics g) {
        int x1Points[] = {getStart().x+30, getEnd().x-17, getStart().x, getEnd().x};
        int y1Points[] = {getStart().y, getEnd().y+1, getStart().y, getEnd().y};
        g.setColor(Color.WHITE);
        g.fillPolygon(x1Points, y1Points, npoints);
    }
    
    protected void drawEventBorder(Graphics g) {
        int x1Points[] = {getStart().x+30, getEnd().x-17, getStart().x, getEnd().x};
        int y1Points[] = {getStart().y, getEnd().y+1, getStart().y, getEnd().y};
        g.setColor(color);
        g.drawPolygon(x1Points, y1Points, npoints);

    }

    @Override
    protected void drawLabel(Graphics g) {
        if (getLabel() != null && !getLabel().equals("")) {
            GraphicsTools.drawString(g, getLabel(), getCenter().x, getEnd().y, GraphicsTools.HorizontalAlignment.center, GraphicsTools.VerticalAlignment.top);
        }
    }

}
