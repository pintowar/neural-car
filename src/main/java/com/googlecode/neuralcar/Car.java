/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar;

import com.googlecode.neuralcar.util.Resources;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;

/**
 *
 * @author thiago
 */
public final class Car {

    public final float ROTATION = 5;
    private Polygon animationPoly;
    private Animation animation;
    private float animationX = 0;
    private float animationY = 0;
    private final Scenario scenario;
    private float rotation = 0;

    public Car(Scenario scenario) throws SlickException {
        this.scenario = scenario;
        init();
    }

    /**
     * Initialize the car information.
     */
    public void init() throws SlickException {
        animation = new Animation();
        animation.setAutoUpdate(true);
        SpriteSheet sheet = new SpriteSheet(Resources.CAR, 24, 24);
        for (int frame = 0; frame < 3; frame++) {
            animation.addFrame(sheet.getSprite(frame, 0), 150);
        }
        rotation = 0;
        animationX = 360;
        animationY = 240;
        animationPoly = new Polygon(new float[]{
                    animationX + 6, animationY + 2,
                    animationX + 18, animationY + 2,
                    animationX + 18, animationY + 22,
                    animationX + 6, animationY + 22
                });
    }

    /**
     * Return if the car has collided with any other entity on the scenario.
     * @return detected collision.
     * @throws SlickException
     */
    public boolean entityCollision() throws SlickException {
        for (int i = 0; i < scenario.getEntitiesSize(); i++) {
            Block entity = (Block) scenario.getEntities().get(i);
            if (animationPoly.intersects(entity.getPoly())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return if the car has collided with a determined entity on the scenario.
     * @param entity determined entity.
     * @return detected collision.
     * @throws SlickException
     */
    public boolean entityCollisionWith(Block entity) throws SlickException {
        if (animationPoly.intersects(entity.getPoly())) {
            return true;
        }
        return false;
    }

    /**
     * Rotate to right.
     * @throws SlickException
     */
    public void rotateRight() throws SlickException {
        rotate(ROTATION);
    }

    /**
     * Rotate to left.
     * @throws SlickException
     */
    public void rotateLeft() throws SlickException {
        rotate(-ROTATION);
    }

    /**
     * Move forward.
     * @throws SlickException
     */
    public void moveForward() throws SlickException {
        move(-1);
    }

    /**
     * Move backward.
     * @throws SlickException
     */
    public void moveBackward() throws SlickException {
        move(1);
    }

    private void move(int i) throws SlickException {
        if (i != -1 & i != 1) {
            throw new SlickException("Move not supported");
        }
        float addX = i * 1.0f * ((float) Math.sin(Math.toRadians(-rotation)));
        float addY = i * 1.0f * ((float) Math.cos(Math.toRadians(-rotation)));
        animationX += addX;
        animationY += addY;
        animationPoly.setCenterX(animationX + 12);
        animationPoly.setCenterY(animationY + 12);
        if (entityCollision()) {
            animationX -= addX;
            animationY -= addY;
            animationPoly.setCenterX(animationX + 12);
            animationPoly.setCenterY(animationY + 12);
        }
    }

    /**
     * Return the center point of the car.
     * @return car center point.
     */
    public Float[] getCenter() {
        float[] point = animationPoly.getCenter();
        return new Float[]{point[0], point[1]};
    }

    private void rotate(float rotate) throws SlickException {
        rotation += rotate;
        rotation %= 360;
        if (rotation < 0) {
            rotation += 360;
        }
        for (int i = 0; i < animation.getFrameCount(); i++) {
            animation.getImage(i).rotate(rotate);
        }
        Transform trans = Transform.createRotateTransform(
                (float) Math.toRadians(rotate));
        Polygon aux = (Polygon) animationPoly.transform(trans);
        aux.setCenterX(animationPoly.getCenterX());
        aux.setCenterY(animationPoly.getCenterY());
        animationPoly = aux;
    }

    /**
     * Return the sum of 9 key points of the polygon determined by the car.
     * @return actual sum of weights points.
     */
    public int situationPoints() {
        //TODO
        int sumWeight = 0;
        sumWeight += scenario.tileWeight(new float[]{animationPoly.getCenterX(),
                    animationPoly.getCenterY()});
        sumWeight += scenario.tileWeight(nextPoint(rotation, (int) (animationPoly.getHeight() / 2)));

        return sumWeight;
    }

    /**
     * This method returns the weights for five key points in a semi circular
     * range (180 degrees) with a certain range (proximity).
     * @param proximity the range to look for weights.
     * @return array with 5 direction points.
     */
    public int[] nextMovesWeights(int proximity) {
        int[] result = new int[]{0, 0, 0, 0, 0};
        result[0] = scenario.tileWeight(nextPoint(rotation + 90, proximity));
        result[1] = scenario.tileWeight(nextPoint(rotation + 45, proximity));
        result[2] = scenario.tileWeight(nextPoint(rotation, proximity));
        result[3] = scenario.tileWeight(nextPoint(rotation - 45, proximity));
        result[4] = scenario.tileWeight(nextPoint(rotation - 90, proximity));
        return result;
    }

    private float[] nextPoint(float degree, float nexts) {
        float x = (float) (animationPoly.getCenterX()
                - ((animationPoly.getWidth() / 2 + nexts)
                * Math.sin(Math.toRadians(-degree))));
        float y = (float) (animationPoly.getCenterY()
                - ((animationPoly.getHeight() / 2 + nexts)
                * Math.cos(Math.toRadians(-degree))));
        return new float[]{x, y};
    }

    /**
     * Method to render the car.
     * @param g
     */
    public void render(Graphics g) {
        float[] a = nextPoint(rotation, 0);
        float[] b = nextPoint(rotation + 90, 0);
        float[] c = nextPoint(rotation - 90, 0);
        float[] d = nextPoint(rotation + 180, 0);
        g.drawLine(animationPoly.getCenterX() , animationPoly.getCenterY() ,
                a[0], a[1]);
        g.drawLine(animationPoly.getCenterX() , animationPoly.getCenterY() ,
                b[0], b[1]);
        g.drawLine(animationPoly.getCenterX() , animationPoly.getCenterY() ,
                c[0], c[1]);
        g.drawLine(animationPoly.getCenterX() , animationPoly.getCenterY() ,
                d[0], d[1]);
        g.draw(animationPoly);
        //g.drawAnimation(animation, animationX, animationY);
    }
}
