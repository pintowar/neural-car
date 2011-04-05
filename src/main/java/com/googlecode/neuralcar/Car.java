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

    public final float ROTATION = (float) Math.toRadians(5);
    private Polygon animationPoly;
    private Animation animation;
    private float animationX = 0;
    private float animationY = 0;
    private final Scenario scenario;
    private float rotation = 0;
    public static final int MAX_POINTS = 9;

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
        final float xi = animationX + 6;
        final float xm = animationX + 12;
        final float xf = animationX + 18;
        final float yi = animationY + 2;
        final float ym = animationY + 12;
        final float yf = animationY + 22;
        animationPoly = new Polygon(new float[]{
                    xi, yi,
                    xm, yi,
                    xf, yi,
                    xf, ym,
                    xf, yf,
                    xm, yf,
                    xi, yf,
                    xi, ym
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

    /**
     * Move the car forward or backwards.
     * @param i -1 for forward, 1 for backwards
     * @throws SlickException
     */
    private void move(int i) throws SlickException {
        if (i != -1 & i != 1) {
            throw new SlickException("Move not supported");
        }
        float c = i * (10.0f - situationPoints()) / 10;
        float addX = c * ((float) Math.sin(-rotation));
        float addY = c * ((float) Math.cos(-rotation));
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

    /**
     * Rotate the car with a certain radian.
     * @param radian
     * @throws SlickException
     */
    private void rotate(float radian) throws SlickException {
        rotation += radian;
        rotation %= (2 * Math.PI);
        if (rotation < 0) {
            rotation += 2 * (Math.PI);
        }
        for (int i = 0; i < animation.getFrameCount(); i++) {
            animation.getImage(i).rotate((float) Math.toDegrees(radian));
        }
        Transform trans = Transform.createRotateTransform(
                (float) radian);
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
        int sumWeight = -MAX_POINTS;
        sumWeight += scenario.tileWeight(new float[]{animationPoly.getCenterX(),
                    animationPoly.getCenterY()});
        int points = animationPoly.getPointCount();
        for (int i = 0; i < points; i++) {
            float[] p = animationPoly.getPoint(i);
            sumWeight += scenario.tileWeight(new float[]{p[0], p[1]});
        }
        return sumWeight;
    }

    /**
     * This method returns the weights for five key points in a semi circular
     * range (PI radians) with a certain range (proximity).
     * @param proximity the range to look for weights.
     * @return array with 5 direction points.
     */
    public int[] nextMovesWeights(int proximity) {
        int[] result = new int[]{0, 0, 0, 0, 0};
        result[0] = scenario.tileWeight(nextPoint(rotation + (float) (Math.PI / 2), proximity));
        result[1] = scenario.tileWeight(nextPoint(rotation + (float) (Math.PI / 4), proximity));
        result[2] = scenario.tileWeight(nextPoint(rotation, proximity));
        result[3] = scenario.tileWeight(nextPoint(rotation - (float) (Math.PI / 4), proximity));
        result[4] = scenario.tileWeight(nextPoint(rotation - (float) (Math.PI / 2), proximity));
        return result;
    }

    /**
     * This method return the float point in certain distance and within a
     * determined radian.
     * @param radian
     * @param distance
     * @return desired point coordinates.
     */
    private float[] nextPoint(float radian, float distance) {
        float x = (float) (animationPoly.getCenterX()
                - ((animationPoly.getWidth() / 2 + distance)
                * Math.sin(-radian)));
        float y = (float) (animationPoly.getCenterY()
                - ((animationPoly.getHeight() / 2 + distance)
                * Math.cos(-radian)));
        return new float[]{x, y};
    }

    /**
     * Method to render the car.
     * @param g
     */
    public void render(Graphics g) {
        /*g.draw(animationPoly);
        for (int i = 90; i >= -90; i -= 45) {
        float[] p = nextPoint(rotation + i, 20);
        g.drawLine(animationPoly.getCenterX(), animationPoly.getCenterY(), p[0], p[1]);
        }*/
        g.drawAnimation(animation, animationX, animationY);
    }
}
