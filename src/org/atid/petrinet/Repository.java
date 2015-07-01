/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.petrinet;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author caiolucena
 */
public class Repository extends RepositoryNode{
     @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        drawSimpleActivityBackground(g);
        drawSimpleActivityBorder(g);
        drawLabel(g);
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
    }

}
