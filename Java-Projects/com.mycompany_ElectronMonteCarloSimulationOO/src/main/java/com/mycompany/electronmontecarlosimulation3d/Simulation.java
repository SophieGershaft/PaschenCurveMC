//@@ -0,0 +1,218 @@
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author sgershaft
 */
public class Simulation {
    // TODO: !!!!!!! FIX SETTINGS TO WORK FOR BOTH PP AND SS 

    private Queue<Electron> queue;
    private StopWatch stopwatch;
    ThreadLocalRandom random;
//    Random random;
    IGeometry geometry; // = new ParallelPlate(random);

    double lambda;
    double lambda_i;
    double count;
    double Ui;

    // FOR DEBUGGING PURPOSES:
    double Nc;
    double Ni;
    double E; // TO DO: RECONSIDER THIS AND CONSIDER USING ONLY E FIELD FROM GEOMETRY
    double delta_t;

    public Simulation(IGeometry geometry, Random random) {
        this.random = ThreadLocalRandom.current();
        this.queue = new LinkedList<Electron>();
        this.stopwatch = new StopWatch();
        this.geometry = geometry;
        // initialize geometry with values from settings
        geometry.initialize();
        this.count = this.geometry.getCount();
    }
    

    public MeanAndError run(boolean printThings, double minCos) {
        stopwatch.start();
        ArrayList<Integer> allElectronCounts = new ArrayList<Integer>();
        ArrayList<Integer> allCollisionCounts = new ArrayList<Integer>();
        ArrayList<Integer> allDeltaEnergyCounts = new ArrayList<Integer>();
        double meanElectrons;
        double eError;
        double meanCollisions;
        double cError;
        double deSumSquare = 0;
        double meanSquareDeltaEnergies;
        double deError;
        int deCount = 0;

        // DEBUGGING PURPOSES:
//        System.out.format("d: %.3f, Ui: %.1f, Nc: %.1f, Ni: %.1f, lambda: %.3f, lambda_i: %.3f, V: %.1f, E: %.1f \n", d, Ui, Nc, Ni, lambda, lambda_i, voltage, E);

        // simulation loop:
        for (int i = 0; i < count; i++) {
            int numElectrons = 1;
            int numCollisions = 0;

            // create single electron and add to queue
            Vector startPosition = geometry.cathodeStart();
            Vector startVelocity = new Vector(0, 0, 0);
            Electron electron = new Electron(startPosition, startVelocity, geometry, printThings);
            queue.add(electron);

            while (queue.size() > 0) {
                Electron currElectron = queue.remove();
                if (printThings) {
//                    System.out.format("position | x: %.3f, y: %.3f, z: %.3f \n", currElectron.position.x, currElectron.position.y, currElectron.position.z);
                }
                double x0;
                double x1;
                boolean ionized = false;
                boolean reachedAnode = false;
                boolean isCathode = false;

                do {
                    double s = Distributions.inverseCDFexponential(lambda, random);
                    // NOTE: THIS IS JUST FOR DEBUGGING !!!!!!!!!!
//                    System.out.println("s: " + s);

                    // update position using vector addition to add components, update velocity
                    x0 = currElectron.position.x;
                    // save delta energy square after updating position and velocity
                    double delta_energy = currElectron.setNewPositionsV2(s);
                    deSumSquare += (delta_energy * delta_energy);
                    deCount++;
                    // update position after
                    x1 = currElectron.position.x;

                    // check if between electrodes
                    reachedAnode = geometry.isAnode(currElectron.position);
                    isCathode = geometry.isCathode(currElectron.position);
                    if (reachedAnode || isCathode) {
//                        System.out.println("reached anode");
                        break;
                    }
                    numCollisions++;
                    // check if ionized
                    double v = currElectron.velocity.getNorm();
                    // checks if energy > Ui
                    ionized = currElectron.checkIonization(v);
                    // adjust energy and (if ionized) launch new electron
                    double energyLoss;

                    if (ionized) {
//                        System.out.println("ionized");
                        numElectrons++;
                        Electron newElectron = new Electron(currElectron.position, startVelocity, geometry, printThings);
                        queue.add(newElectron);
                        // energy loss will later subtract Ui amount of energy
                        energyLoss = this.Ui;
                    } else {
                        // if not ionized, subtract smaller amount of energy
                        double randombit = ((Math.random() * 0.4) + 0.1);
                        // FOR DEBUGGING PURPOSES
//                        double randombit = ((random.nextDouble() * 0.4) + 0.1);
                        // subtract small amount of energy and scatter
                        energyLoss = this.Ui * randombit;
                    }

                    // NEW!!!
                    // forward scattering
                    currElectron.forwardScatter(energyLoss, minCos);
                } while (true);
            }
            allElectronCounts.add(numElectrons);
            allCollisionCounts.add(numCollisions);
            allDeltaEnergyCounts.add(deCount);
        }
        stopwatch.stop();

        System.out.println("Elapsed: " + stopwatch.formatTime());
        // NOTE: might want stats stuff to go in a diff place
        // find mean electrons and collisions
        int eSum = 0;
        for (int eCount : allElectronCounts) {
            eSum += eCount;
        }
        int cSum = 0;
        for (int colCount : allCollisionCounts) {
            cSum += colCount;
        }
        // mean over all the electrons launched to find out mean per sim run
        meanElectrons = eSum / count;
        meanCollisions = cSum / count;

        // finish Root Mean Square (RMS) of delta energies
        meanSquareDeltaEnergies = deSumSquare / deCount;
//        double RMS = Math.sqrt(meanSquareDeltaEnergies);
//        double checkRMS = 0.001 * voltage/Nc;
//        boolean decreaseDeltaT = !(RMS < checkRMS);
//        System.out.format("should decrease delta_t? %b | RMS delta_energy: %10.5f | 0.001*(V/Nc): %10.5f | delta_t: %10.5f \n", decreaseDeltaT, RMS, checkRMS, delta_t);

        // find standard deviation
        double SSR_e = 0;
        double stdev_e;
        double SSR_c = 0;
        double stdev_c = 0;
        for (int currCount : allElectronCounts) {
            SSR_e += (Math.abs(meanElectrons - currCount) * Math.abs(meanElectrons - currCount));
        }
        for (int currColCount : allCollisionCounts) {
            SSR_c += (Math.abs(meanCollisions - currColCount) * Math.abs(meanCollisions - currColCount));
        }
        // find error on the mean
        stdev_e = Math.sqrt(SSR_e / count);
        eError = stdev_e / Math.sqrt(count);
        stdev_c = Math.sqrt(SSR_c / count);
        cError = stdev_c / Math.sqrt(count);
        // package for returning
        MeanAndError result = new MeanAndError();
        result.mean = meanElectrons;
        result.error = eError;
        result.mean_c = meanCollisions;
        result.error_c = cError;
        result.Nc = this.Nc;
        result.Ni = this.Ni;
        result.lambda_i = lambda_i;

        return result;
    }

}
