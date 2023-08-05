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
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author sgershaft
 */
public class Simulation {
    
    private Queue<Electron> queue;
    private StopWatch stopwatch;
    Random random;
    IGeometry geometry; 

    double lambda;
    double lambda_i;
    double count;
    double Ui;

    // FOR DEBUGGING PURPOSES:
    double Nc;
    double Ni;
    double delta_t;

    public Simulation(IGeometry geometry, Random random) {
        this.geometry = geometry;
        this.random = random;
        this.Nc = this.geometry.getNc();
        this.Ni = this.geometry.getNi();
        this.queue = new LinkedList<Electron>();
        this.stopwatch = new StopWatch();
        this.lambda = this.geometry.getLambda();
        this.lambda_i = this.geometry.getLambdaI();
        this.count = this.geometry.getCount();
        this.Ui = this.geometry.getUI();
        Interpolation.makeScPotential("C:\\Users\\sgershaft\\github\\Paschen-Paper-January\\Java-Projects\\com.mycompany_ElectronMonteCarloSimulationOO\\src\\main\\resources\\phi.txt");
    }
    

    public MeanAndError run(double minCos) {
        stopwatch.start();
        ArrayList<Integer> allElectronCounts = new ArrayList<Integer>();
        ArrayList<Integer> allIonCounts = new ArrayList<Integer>();
        ArrayList<Integer> allCollisionCounts = new ArrayList<Integer>();
        ArrayList<Integer> allDeltaEnergyCounts = new ArrayList<Integer>();
        double meanElectrons;
        double eError;
        double meanIons;
        double iError;
        double meanCollisions;
        double cError;
        double deSumSquare = 0;
        double meanSquareDeltaEnergies;
        double deError;
        int deCount = 0;

        // DEBUGGING PURPOSES:
        System.out.format("Ui: %.1f, Nc: %.1f, Ni: %.1f, lambda: %.3f, lambda_i: %.3f \n", Ui, Nc, Ni, lambda, lambda_i);
//        System.out.format("Ui: %.1f, Ni: %.1f, lambda: %.3f\n", Ui, Ni, lambda); // SC version

        // simulation loop:
        for (int i = 0; i < count; i++) {
            int numElectrons = 1;
            int numIons = 0;
            int numCollisions = 0;

            // create single electron and add to queue
            Vector startPosition = geometry.cathodeStart();
//            System.out.format("start position: %10.3f %10.3f %10.3f \n", startPosition.x, startPosition.y, startPosition.z);
            Vector startVelocity = new Vector(0, 0, 0);
            Electron electron = new Electron(startPosition, startVelocity, geometry);
            queue.add(electron);

            while (queue.size() > 0) {
                Electron currElectron = queue.remove();
                boolean ionized = false;
                boolean reachedAnode = false;
                boolean isCathode = false;

                do {
                    double s = Distributions.inverseCDFexponential(this.lambda, random);
//                    System.out.println("new s: " + s);

                    // save delta energy square after updating position and velocity
                    double delta_energy = currElectron.travel(s);
                    deSumSquare += (delta_energy * delta_energy);
                    deCount++;

                    // check if between electrodes
                    reachedAnode = geometry.isAnode(currElectron.position);
//                    isCathode = geometry.isCathode(currElectron.position);
                    // NOTE this can cause problems if the electron doesn't move the first time
                    if (reachedAnode || isCathode) {
//                        System.out.println("reached anode");
                        break;
                    }
                    numCollisions++;
                    // check if ionized: 
                    // checks if energy > Ui
                    ionized = currElectron.checkIonization();
                    // adjust energy and (if ionized) launch new electron
                    double energyLoss;

                    if (ionized) {
//                        System.out.println("ionized");
                        Electron newElectron = new Electron(currElectron.position, startVelocity, geometry);
                        queue.add(newElectron);
                        numElectrons++;
                        numIons++;
                        // energy loss will later subtract Ui amount of energy
                        energyLoss = this.Ui;
                    } else {
                        // if not ionized, subtract smaller amount of energy
//                        double randombit = ((Math.random() * 0.4) + 0.1);
                        // FOR DEBUGGING PURPOSES
                        double randombit = ((random.nextDouble() * 0.4) + 0.1);
                        // subtract small amount of energy and scatter
                        energyLoss = this.Ui * randombit;
                    }

                    // forward scattering
                    currElectron.forwardScatter(energyLoss, minCos);
                } while (true);
            }
            allElectronCounts.add(numElectrons);
            allIonCounts.add(numIons);
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
        int iSum = 0;
        for (int iCount : allIonCounts) {
            iSum += iCount;
        }
        // mean over all the electrons launched to find out mean per sim run
        meanElectrons = ((double)eSum) / count;
        meanIons = ((double)iSum) / count;
        meanCollisions = ((double)cSum) / count;

        // finish Root Mean Square (RMS) of delta energies
        meanSquareDeltaEnergies = deSumSquare / deCount;
//        double RMS = Math.sqrt(meanSquareDeltaEnergies);
//        double checkRMS = 0.001 * voltage/Nc;
//        boolean decreaseDeltaT = !(RMS < checkRMS);
//        System.out.format("should decrease delta_t? %b | RMS delta_energy: %10.5f | 0.001*(V/Nc): %10.5f | delta_t: %10.5f \n", decreaseDeltaT, RMS, checkRMS, delta_t);

        // find standard deviation
        double SSR_e = 0;
        double stdev_e;
        double SSR_i = 0;
        double stdev_i;
        double SSR_c = 0;
        double stdev_c = 0;
        for (int currCount : allElectronCounts) {
            SSR_e += (Math.abs(meanElectrons - currCount) * Math.abs(meanElectrons - currCount));
        }
        for (int currICount : allIonCounts) {
            SSR_i += (Math.abs(meanIons - currICount) * Math.abs(meanIons - currICount));
        }
        for (int currColCount : allCollisionCounts) {
            SSR_c += (Math.abs(meanCollisions - currColCount) * Math.abs(meanCollisions - currColCount));
        }
        // find error on the mean
        stdev_e = Math.sqrt(SSR_e / count);
        eError = stdev_e / Math.sqrt(count);
        stdev_i = Math.sqrt(SSR_i / count);
        iError = stdev_i / Math.sqrt(count);
        stdev_c = Math.sqrt(SSR_c / count);
        cError = stdev_c / Math.sqrt(count);
        // package for returning
        MeanAndError result = new MeanAndError();
        // IMPORTANT!!!!! IF I WANT TO COUNT AND REPORT ELECTRONS, JUST SWITCH THIS FROM MEAN IONS TO MEAN ELECTRONS BELOW
        result.mean = meanIons;
        result.error = iError;
        result.mean_c = meanCollisions;
        result.error_c = cError;
        result.Nc = this.Nc;
        result.Ni = this.Ni;
        result.lambda_i = lambda_i;

        return result;
    }

}
