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
public interface Controller {

    void act(Car car, GameContainer container, int delta) throws SlickException;
}
