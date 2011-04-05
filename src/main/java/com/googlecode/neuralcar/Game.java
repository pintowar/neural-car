/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import com.googlecode.neuralcar.controller.Controller;
import com.googlecode.neuralcar.controller.HumanController;
import com.googlecode.neuralcar.controller.ResilientNeuralController;
import com.googlecode.neuralcar.util.Resources;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 *
 * @author thiago
 */
public class Game extends BasicGame {

    private Scenario scenario;
    private Car car;
    private Controller controller;

    public Game(String title) {
        this(title, new HumanController());
    }

    public Game(String title, Controller controller) {
        super(title);
        this.controller = controller;
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setVSync(true);
        scenario = new Scenario(Resources.SCENARIO);
        car = new Car(scenario);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        controller.act(car, container, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        scenario.render(0, 0);
        car.render(g);
    }
}
