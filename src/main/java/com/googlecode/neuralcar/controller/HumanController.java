/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar.controller;

import com.googlecode.neuralcar.Car;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 *
 * @author thiago
 */
public class HumanController implements Controller {

    @Override
    public void act(Car car, GameContainer container, int delta) throws SlickException {
        if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
            car.rotateLeft();
        }
        if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
            car.rotateRight();
        }
        if (container.getInput().isKeyDown(Input.KEY_UP)) {
            car.moveForward();
        }
        if (container.getInput().isKeyDown(Input.KEY_DOWN)) {
            car.moveBackward();
        }
        if (container.getInput().isKeyDown(Input.KEY_SPACE)) {
            System.out.println("Situation: " + car.situationPoints());
        }
    }
}
