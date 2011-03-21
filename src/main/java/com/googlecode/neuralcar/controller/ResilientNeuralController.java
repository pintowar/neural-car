/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.neuralcar.controller;

import com.googlecode.neuralcar.Car;
import java.util.ArrayList;
import java.util.List;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 *
 * @author thiago
 */
public class ResilientNeuralController implements Controller {

    private final int DEFALUT_PROXIMITY = 15;
    private final int MAX_ITERATION = 25;
    private BasicNetwork network;
    private int iteration = 0;
    private List<NeuralDataPair> trainData = new ArrayList<NeuralDataPair>();
    private boolean training = false;

    public ResilientNeuralController() {
        initiateNeuralNetwork();
    }

    private void initiateNeuralNetwork() {
        if (network == null) {
            network = new BasicNetwork();

            Layer outputLayer = new BasicLayer(new ActivationTANH(), true, 1);
            Layer hiddenLayer1 = new BasicLayer(new ActivationTANH(), true, 7);
            Layer inputLayer = new BasicLayer(new ActivationTANH(), false, 5);

            Synapse synapse1 = new WeightedSynapse(hiddenLayer1, outputLayer);
            Synapse synapse2 = new WeightedSynapse(inputLayer, hiddenLayer1);

            hiddenLayer1.addSynapse(synapse1);
            inputLayer.addSynapse(synapse2);

            network.tagLayer("INPUT", inputLayer);
            network.tagLayer("OUTPUT", outputLayer);
            network.setLogic(new FeedforwardLogic());

            network.getStructure().finalizeStructure();
            network.reset();
        }
    }

    float normalizeInput(int weight) {
        switch (weight) {
            case 1:
                return -1.0f;
            case 2:
                return 0.0f;
            default:
                return 1.0f;
        }
    }

    int denormalizeOutput(float output) {
        if (output <= -0.3333) {
            return -1;
        } else if (output > -0.3333 && output <= 0.3333) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void act(Car car, GameContainer container, int delta) throws SlickException {
        if (container.getInput().isKeyDown(Input.KEY_SPACE)) {
            car.init();
            return;
        }
        if (iteration++ < MAX_ITERATION) {
            move(car);
        } else {
            iteration = 0;
            train();
        }
    }

    void move(Car car) throws SlickException {
        int[] moves = car.nextMovesWeights(DEFALUT_PROXIMITY);
        int situationBefore = car.situationPoints();
        Float[] pointBefore = car.getCenter();

        double[] normInput = new double[]{normalizeInput(moves[0]),
            normalizeInput(moves[1]), normalizeInput(moves[2]),
            normalizeInput(moves[3]), normalizeInput(moves[4])/*,
            ((10.0f - car.situationPoints()) / 5) - 1*/};

        NeuralData inputData = new BasicNeuralData(normInput);
        NeuralData outputData = network.compute(inputData);
        int rotation = denormalizeOutput((float) outputData.getData(0));
        if (rotation == 1) {
            car.rotateLeft();
        } else if (rotation == -1) {
            car.rotateRight();
        }
        car.moveForward();

        int situationAfter = car.situationPoints();
        Float[] pointAfter = car.getCenter();

        float movement = moveDistance(pointBefore, pointAfter);

        if (situationAfter <= situationBefore) {
            //if car not stucked
            if (movement > 0) {
                NeuralData input = new BasicNeuralData(normInput);
                NeuralData output = new BasicNeuralData(new double[]{rotation});
                for (int i = 0; i < Car.MAX_POINTS - situationAfter; i++) {
                    trainData.add(new BasicNeuralDataPair(input, output));
                }
            } else {
                System.out.println("Stucked!!");
                randomMoves(car);
            }
        }
    }

    void train() {
        //atomic training
        if (!training) {
            training = true;
            NeuralDataSet dataSet = new BasicNeuralDataSet(trainData);
            Train train = new ResilientPropagation(network, dataSet);
            int epoch = 0;

            do {
                train.iteration();
                if(epoch > 0)System.out.println("Epoch #" + epoch + " Error:" + (train.getError() * 100.0) + "%");
                epoch++;
            } while (train.getError() > 0.003);
            trainData.clear();
            training = false;
        }
    }

    float moveDistance(Float[] from, Float[] to) {
        float dx = to[0] - from[0];
        float dy = to[1] - from[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    void randomMoves(Car car) throws SlickException {
        for (int i = 0; i < 100; i++) {
            if (Math.random() < 0.5) {
                car.rotateLeft();
            } else {
                car.rotateRight();
            }
            if (Math.random() < 0.5) {
                car.moveForward();
            } else {
                car.moveForward();
            }
        }
    }
}
