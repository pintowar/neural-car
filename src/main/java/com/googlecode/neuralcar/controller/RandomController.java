/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar.controller;

import com.googlecode.neuralcar.Car;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 *
 * @author thiago
 */
public class RandomController implements Controller {

    @Override
    public void act(Car car, GameContainer container, int delta) throws SlickException {
        if (0.3 < Math.random()) {
            car.moveForward();
        } else {
            car.moveBackward();
        }
        if (0.5 >= Math.random()) {
            car.rotateLeft();
        } else {
            car.rotateRight();
        }
    }
}
