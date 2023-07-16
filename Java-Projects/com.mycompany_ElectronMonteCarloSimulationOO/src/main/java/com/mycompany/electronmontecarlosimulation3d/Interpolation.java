package com.mycompany.electronmontecarlosimulation3d;

/*
A program to interpolate between phi values from the feb-5-phis.txt file

Gives E field values and potential values for the SC case
 */


import java.io.*;
import java.util.*;


public class Interpolation {
    
    // store phi 2d array
    static double[][] scpotential;
    
    // grid spacing for the phi.txt file
    static final double gs = 0.01;
    
//    public static void main(String[] args) {
//        // load scpotential from file (see function below)
//        makeScPotential("C:\\Users\\sgershaft\\github\\Paschen-Paper-January\\Java-Projects\\com.mycompany_ElectronMonteCarloSimulationOO\\src\\main\\resources");
//       
//        // do a test. expected output: {0.130789, 0.161607, 0.359219}
//        System.out.println(Arrays.toString(getEFieldSC(10, 2.784, 3.44, 5.775)));
//        
//        // do a test. expected output: 0.520356
//        System.out.println(getPotentialSC(10, 2.784, 3.44, 5.775));
//    }
    
    // populate the scpotential 2d array using the phi.txt file
    public static void makeScPotential(String pathname) {
        scpotential = new double[731][731];
        try (BufferedReader br = new BufferedReader(new FileReader(pathname))) {
            String line;
            int r = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.trim().split("\\s+");
                double[] row = new double[731];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Double.parseDouble(values[i]);
                }
                scpotential[r] = row;
                r++;
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }
    }
   
    // interpolate to get the e field
    // takes in a voltage and position and returns a vector with the e field
    public static double[] getEFieldSC(double v, double x, double y, double zraw) {
        // get the constants we need i.e., rho, i, j, alpha, and beta
        double rho = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
        double z;
        boolean flip = false;
        if (zraw < 0.0) {
            z = Math.abs(zraw);
            flip = true;
        } else {
            z = zraw;
        }
        int i = (int) Math.floor((double) z/gs);
        int j = (int) Math.floor((double) rho/gs);
        double alpha = z / gs - (double) i;
        double beta = rho / gs - (double) j;
        
        // do the interpolation in the radial direction
        double erho = (1 - alpha) * (scpotential[i][j] - scpotential[i][j + 1]) / gs 
                + alpha * (scpotential[i + 1][j] - scpotential[i + 1][j + 1]) / gs;
        // now the z direction
        double ez = (1 - beta) * (scpotential[i][j] - scpotential[i + 1][j]) / gs
          + beta * (scpotential[i][j + 1] - scpotential[i + 1][j + 1]) / gs ;
     
        // post process and return
        if (flip) {
            ez = -ez;
        }
        double ex = -1 * v * x * erho / rho;
        double ey = -1 * v * y * erho / rho;
        ez = - 1 * v * ez;
        double[] res = {ex, ey, ez};
        return res;
    }
    
    // interpolate to get the potential
    // takes in a voltage and position and returns a scalar representing the potential
    public static double getPotentialSC(double v, double x, double y, double z) {
        // compute constants
        double rho = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
        int i = (int) Math.floor((double) z/gs);
        int j = (int) Math.floor((double) rho/gs);
        double alpha = z / gs - (double) i;
        double beta = rho / gs - (double) j;
        
        // do the interpolation
        double res = -1 * v * ((1 - beta) * (1 - alpha) * scpotential[i][j] +
                (1 - beta) * (alpha) * scpotential[i+1][j] +
                (beta) * (1 - alpha) * scpotential[i][j+1] +
                (beta) * (alpha) * scpotential[i+1][j+1]);
        
        return res;
    }
}