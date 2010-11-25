/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author thiago
 */
public class Block {

    private Polygon poly;

    public Block(int x, int y, int[] points) {
        poly = new Polygon(new float[]{
                    x + points[0], y + points[1],
                    x + points[2], y + points[3],
                    x + points[4], y + points[5],
                    x + points[6], y + points[7],});
    }

    public void draw(Graphics g) {
        g.draw(poly);
    }

    public Polygon getPoly() {
        return poly;
    }
}
