/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 *
 * @author thiago
 */
public class Main {

    static int height = 480;
    static int width = 704;
    static boolean fullscreen = false;
    static boolean showFPS = true;
    static String title = "Slick Basic Game Template";
    static int fpslimit = 60;

    public static void main(String[] args) throws SlickException {
    AppGameContainer app = new AppGameContainer(new Game(title));
    app.setDisplayMode(width, height, fullscreen);
    app.setSmoothDeltas(true);
    app.setTargetFrameRate(fpslimit);
    app.setShowFPS(showFPS);
    app.start();
    }
    /*public static void main(String[] args)
            throws SlickException {
        AppGameContainer app =
                new AppGameContainer(new SlickBasicGame());

        app.setDisplayMode(800, 600, false);
        app.start();
    }*/
}
