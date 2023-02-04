//@@ -0,0 +1,212 @@
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import java.util.ArrayList;
import org.apache.commons.math3.analysis.function.Asinh;

/**
 *
 * @author sgershaft
 */
public class ElectronFresh {

    Vector position; // in meters
    Vector velocity; // number, fraction of C (just like beta)

//    boolean showPath;// showPath enables printing positions of collisions TO DO
    public static final double e = 1; // 1 electron charge (charge of electron)
    public static final double m = 511000; // in eV bc m_e = 511,000 eV
    public double E = SettingsFresh.getInstance().getE();
    public double delta_t = SettingsFresh.getInstance().getDeltaT();

    public ElectronFresh(Vector startPosition, Vector startVelocity, boolean printThings) {
        this.position = startPosition;
        this.velocity = startVelocity;
    }

    public void printPosVel() {
//        System.out.format("position: %12.8f %12.8f %12.8f    velocity: %12.8f %12.8f %12.8f \n", position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }

    public double setNewPositionsV2(double s) {

        printPosVel();
//        System.out.format("s: %10.5f \n", s);
        // save old things
        double x0 = position.x;
        double K_i = 0.5 * this.m * velocity.Square();

        double path = 0;
        // Euler's method
        while (path < s) {
//           System.out.format("path: %10.5f \n", path);
            // increment position
            Vector deltaPosition = velocity.multiplyByScalar(delta_t); 
            double delta = deltaPosition.getNorm();
            double correctionFactor = 1;
            // if next step will overshoot (last step of Euler's method)
            if (path + delta > s) {
                // last step --> correct deltaPosition
                // correct by decreasing s by rest of path over distance computed
                correctionFactor = (s - path)/delta;
                deltaPosition = deltaPosition.multiplyByScalar(correctionFactor);
            }
            path += delta;
            position = position.addVectors(deltaPosition);

            // increment velocity
            double constant = ((e * E) / m) * delta_t * correctionFactor;
            Vector deltaVelocity = new Vector(constant, 0, 0);
            velocity = velocity.addVectors(deltaVelocity);
  
//           System.out.format("path: %10.5f \n", path);
        }

        printPosVel();

        // find total energy
        double x1 = this.position.x;

        // should be diff between U when working in diff geometry
//        double deltaU = e * SettingsFresh.getInstance().getE() * (x1 - x0);
        double K_f = 0.5 * m * this.velocity.Square();
        double delta_energy = ((K_f - K_i) - (e * SettingsFresh.getInstance().getE() * (x1-x0)));

//        System.out.format("K_i: %.3f, K_f: %.3f, deltaU: %.3f \n", K_i, K_f, deltaU);
//        System.out.println("delta energy (should be ~0)" + delta_energy);
        
        // NEW!!!
        // return a delta_energy for RMS
        return delta_energy;
    }

    public void forwardScatter(double energyLoss, double minCos) {
        double Ki = 0.5 * m * velocity.Square();
        double Kf = Ki - energyLoss;

        if (Kf < 0.0) {
            velocity.x = 0;
            velocity.y = 0;
            velocity.z = 0;
            return;
        }

        // make a set of basis vectors where basis1 is the original velocity direction
        Vector basis1 = velocity.getUnitVector();
        Vector basis2 = basis1.constructPerpendicular();
        Vector basis3 = basis1.getCrossProduct(basis2);

        // DEBUG VERSION:
        double ran1 = Main.random.nextDouble();
        double ran2 = Main.random.nextDouble();
//        System.out.format("%20.15f %20.15f \n", ran1, ran2);
        Main.randomNums.add(ran1);
        Main.randomNums.add(ran2);
        
        double cosTheta = (1.0 - minCos) * ran1 + minCos;
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
        double phi = 2.0 * Math.PI * ran2;
        
//        double cosTheta = (1.0 - minCos) * Math.random() + minCos; // random in range minCos to 1.0
//        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
//        double phi = 2.0 * Math.PI * Math.random(); // random from 0 to 2pi

        // v magnitude
        double v1 = Math.sqrt((2.0 * Kf) / m);
        Vector v_b1 = basis1.multiplyByScalar(cosTheta * v1);
        Vector v_b2 = basis2.multiplyByScalar(sinTheta * Math.cos(phi) * v1);
        Vector v_b3 = basis3.multiplyByScalar(sinTheta * Math.sin(phi) * v1);
        
        Vector v_b1_b2 = v_b1.addVectors(v_b2);
        Vector velocity1 = v_b1_b2.addVectors(v_b3);
        velocity = velocity1;
    }

      
    // check ionization
    boolean checkIonization(double v) {
        double energy = 0.5 * this.m * v * v; // in eV

        // FOR DEBUGGING
//        System.out.println("energy: " + energy);
        double Ui = SettingsFresh.getInstance().getUI();
        if (energy >= Ui) {
            return true;
        }
        return false;
    }
}
