/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import com.googlecode.neuralcar.controller.Controller;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import static com.googlecode.neuralcar.util.ReflectionUtils.*;

/**
 *
 * @author thiago
 */
public class Main {

    private int height = 480;
    private int width = 704;
    private boolean fullscreen = false;
    private boolean showFPS = true;
    private String title = "Slick Basic Game Template";
    private int fpslimit = 60;

    public static void main(String[] args) throws Exception {
        Class clazz = Controller.class;
        Package pack = clazz.getPackage();
        Class[] options = implementsFrom(getClasses(pack.getName()), clazz);
        String[] names = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            names[i] = options[i].getSimpleName();
        }
        int resp = JOptionPane.showOptionDialog(null, "Select a Controller", "Select a Controller",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        Controller controller = (Controller) options[resp].newInstance();
        Main main = new Main();
        main.startSimulation(controller);
    }

    private void startSimulation(Controller controller) throws SlickException{
        AppGameContainer app = new AppGameContainer(new Game(title, controller));
        app.setDisplayMode(width, height, fullscreen);
        app.setSmoothDeltas(true);
        app.setTargetFrameRate(fpslimit);
        app.setShowFPS(showFPS);
        app.start();
    }

    
}
