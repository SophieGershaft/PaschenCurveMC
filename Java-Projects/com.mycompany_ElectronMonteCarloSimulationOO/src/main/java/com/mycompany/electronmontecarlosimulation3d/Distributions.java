/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import java.util.Random;

/**
 *
 * @author sgershaft
 */
public class Distributions {

    // lambda is mean free path
    public static double inverseCDFexponential(double lambda, Random random) {
        // cumulative distribution function
        // makes exponential distribution from uniform distribution
        double u = random.nextDouble();
//        System.out.format("%20.15f \n", u);
//        System.out.format("lambda inside CDF: %.5f \n", lambda);
        Main.randomNums.add(u);
        double s = -lambda * Math.log(1 - u);
//        System.out.format("s = -%.15f * log(1 - %.15f) = %.15f \n", lambda, u, s);
        return s;
    }   
    
}
