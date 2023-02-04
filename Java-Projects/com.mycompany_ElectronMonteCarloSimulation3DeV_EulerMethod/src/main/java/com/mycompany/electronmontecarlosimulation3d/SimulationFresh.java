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
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author sgershaft
 */
public class SimulationFresh {

    private Queue<ElectronFresh> queue;
    private StopWatch stopwatch;

    double d = SettingsFresh.getInstance().getD();
    double voltage = SettingsFresh.getInstance().getVoltage();
    double lambda = SettingsFresh.getInstance().getLambda();
    double lambda_i = SettingsFresh.getInstance().getLambdaI();
    double count = SettingsFresh.getInstance().getCount();

    // FOR DEBUGGING PURPOSES:
    double Ui = SettingsFresh.getInstance().getUI();
    double Nc = SettingsFresh.getInstance().getNc();
    double Ni = SettingsFresh.getInstance().getNi();
    double E = SettingsFresh.getInstance().getE();
    double delta_t = SettingsFresh.getInstance().getDeltaT();

    public SimulationFresh() {
        this.queue = new LinkedList<ElectronFresh>();
        this.stopwatch = new StopWatch();
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
        int deCount = 0;

        // DEBUGGING PURPOSES:
//        System.out.format("d: %.3f, Ui: %.1f, Nc: %.1f, Ni: %.1f, lambda: %.3f, lambda_i: %.3f, V: %.1f, E: %.1f \n", d, Ui, Nc, Ni, lambda, lambda_i, voltage, E);

        // simulation loop:
        for (int i = 0; i < count; i++) {
            int numElectrons = 1;
            int numCollisions = 0;
            
            // create single electron and add to queue
            Vector startPosition = new Vector(0, 0, 0);
            Vector startVelocity = new Vector(0, 0, 0);
            ElectronFresh electron = new ElectronFresh(startPosition, startVelocity, printThings);
            queue.add(electron);

            while (queue.size() > 0) {
                ElectronFresh currElectron = queue.remove();
                if (printThings) {
//                    System.out.format("position | x: %.3f, y: %.3f, z: %.3f \n", currElectron.position.x, currElectron.position.y, currElectron.position.z);
                }
                double x0;
                double x1;
                boolean ionized = false;
                boolean reachedAnode = false;
                
                do {
                    double s = Distributions.inverseCDFexponential(lambda);
                    // NOTE: THIS IS JUST FOR DEBUGGING !!!!!!!!!!
//                    System.out.println("s: " + s);

                    // NEW!!!
                    // update position using vector addition to add components, update velocity
                    x0 = currElectron.position.x;
                    // save delta energy square after updating position and velocity
                    double delta_energy = currElectron.setNewPositionsV2(s);
                    deSumSquare+= (delta_energy * delta_energy);
                    deCount++;
                    // update position after
                    x1 = currElectron.position.x;
                    
                    // check if reached anode
                    reachedAnode = (x1 >= d);
                    if (reachedAnode) {
//                        System.out.println("reached anode");
                        break;
                    }
                    numCollisions++;
//                    System.out.println("numCollisions: " + numCollisions);
                    // check if ionized
                    double v = currElectron.velocity.getNorm();
                    // checks if energy > Ui
                    ionized = currElectron.checkIonization(v);
                    // adjust energy and (if ionized) launch new electron
                    double energyLoss;

                    if (ionized) {
//                        System.out.println("ionized");
                        numElectrons++;
                        ElectronFresh newElectron = new ElectronFresh(currElectron.position, startVelocity, printThings);
                        queue.add(newElectron);
                        // energy loss will later subtract Ui amount of energy
                        energyLoss = SettingsFresh.getInstance().getUI();
                    } else {
                        // if not ionized, subtract smaller amount of energy
//                        double randombit = ((Math.random() * 0.4) + 0.1);
                        
                    // DEBUGGING VERSION:
                        double ran3 = Main.random.nextDouble();
//                        System.out.format("%20.15f \n", ran3);
                        Main.randomNums.add(ran3);
                        double randombit = ((ran3 * 0.4) + 0.1);
                        // subtract small amount of energy and scatter
                        energyLoss = SettingsFresh.getInstance().getUI() * randombit;
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

//        System.out.println("Elapsed: " + stopwatch.formatTime());
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
        double RMS = Math.sqrt(meanSquareDeltaEnergies);
        double checkRMS = 0.001 * voltage/Nc;
        boolean decreaseDeltaT = !(RMS < checkRMS);
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
        result.Nc = SettingsFresh.getInstance().getNc();
        result.Ni = SettingsFresh.getInstance().getNi();
        result.lambda_i = lambda_i;

        return result;
    }

}