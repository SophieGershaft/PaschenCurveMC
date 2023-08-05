/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

/**
 *
 * @author sgershaft
 */
public class MeanAndError {
    public double mean;
    public double error;
    public double mean_c;
    public double error_c;
    public double Nc;
    public double Ni;
    public double lambda_i;
    public double[][] electronData;
    
    @Override
    public String toString() {
        return String.format("mean ions: %.3f +-%.3f, Nc: %.3f, Ni: %.3f lambda_i: %.3f, mean collisions: %.3f +-%.3f \n", mean, error, Nc, Ni, lambda_i, mean_c, error_c);
    }
    
}