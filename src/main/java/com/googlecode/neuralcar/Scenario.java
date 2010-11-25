/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author thiago
 */
public class Scenario {

    public TiledMap tmap;
    public int mapWidth;
    public int mapHeight;
    private int square[] = {1, 1, 31, 1, 31, 31, 1, 31}; //square shaped tile
    public ArrayList<Block> entities;

    public Scenario(String ref) throws SlickException {
        entities = new ArrayList<Block>();
        tmap = new TiledMap(ref);

        mapWidth = tmap.getWidth() * tmap.getTileWidth();
        mapHeight = tmap.getHeight() * tmap.getTileHeight();
        int objectLayer = tmap.getLayerIndex("Objects");
        for (int x = 0; x < tmap.getWidth(); x++) {
            for (int y = 0; y < tmap.getHeight(); y++) {
                int tileID = tmap.getTileId(x, y, objectLayer);
                if (asList(new Integer[]{3, 17, 18, 33, 34}).contains(tileID)) {
                    entities.add(new Block((x - 1) * tmap.getTileWidth(),
                            (y - 1) * tmap.getTileHeight(), square));
                }
            }
        }
    }

    public int tileWeight(float[] points) {
        int x = (int) (points[0] / tmap.getTileWidth()) + 1;
        int y = (int) (points[1] / tmap.getTileHeight()) + 1;
        Integer objects = tmap.getTileId(x, y, tmap.getLayerIndex("Objects"));
        Integer street = tmap.getTileId(x, y, tmap.getLayerIndex("Street"));
        return (asList(new Integer[]{3, 17, 18, 33, 34}).contains(objects)) ? 3
                : (asList(new Integer[]{5, 6, 21, 22, 35, 36, 37}).contains(street)) ? 1
                : 2;
    }

    public ArrayList<Block> getEntities() {
        return entities;
    }

    public int getEntitiesSize() {
        return entities.size();
    }

    public void render(int x, int y) {
        tmap.render(x - tmap.getTileWidth(), y - tmap.getTileHeight());
    }
}
