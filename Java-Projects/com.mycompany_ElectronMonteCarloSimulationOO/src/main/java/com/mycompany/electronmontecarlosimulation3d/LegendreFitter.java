package com.mycompany.electronmontecarlosimulation3d;

import java.util.ArrayList;

public class LegendreFitter {
    
    // using even orders from 0-10, so 6 dimensions
    public final int dim = 6;
    public ArrayList<double[]> legendreData;
    public double[][] sumPP; // holds big matrix of twiddles
    public double[] sumPY; // holds vector (dependent on experimental data)
    public double sumYY; // simple sum of squared ion counts
    public double[][] cholesky; // matrix after cholesky decomp
    public double[] choleskyP; // diagonal of cholesky
    public double[] choleskyB; // coefficients for legendre polynomials
    public double variance; // variance of the fit, used for getting error
    
    // constructor
    public LegendreFitter(ArrayList<double[]> legendreData) {
        this.legendreData = legendreData; // {cos(theta), electrons}
    }
    
    // this is the workhorse: call this to get your result (don't need to do anything else)
    // given the data, returns the max electrons with error
    public double[] findMax() {
        computeSums(); // get the matricies and vectors we need (see Sophie's  notes)
        solveFit(); // solve for the coefficients, stored in choleskyB
        
        // now, we find the best z and return
        
        this.variance = (sumYY - Cholesky.dotProduct(choleskyB, sumPY)) / (double) ((legendreData.size() - dim));
     
        double bestZ = 0.0;
	double bestIons = fitValue(0.0);
	double bestError = fitError(0.0);
        
        for (int i = 1; i <= 100; i++) {
            double z = i / 100.0;
            double val = fitValue(z);
            if (val > bestIons) {
                bestZ = z;
                bestIons = val;
                bestError = fitError(z);
            }
        }
        double[] res = { bestZ, bestIons, bestError };
        return res;
    }
    
    // get a twiddle using two legendre polynomials
    public double computePTwiddle(int o1, int o2) {
        double res = 0.0;
        for (double[] pair : legendreData) {
            double xi = pair[0];
            res += (LegendrePolynomials.P(o1, xi) * LegendrePolynomials.P(o2, xi));
        }
        return res;
    }
    
    // get a twiddle using one legendre polynomial and one y 
    public double computeYTwiddle(int o) {
        double res = 0.0;
        for (double[] pair : legendreData) {
            double xi = pair[0];
            double yi = pair[1];
            res += (LegendrePolynomials.P(o, xi) * yi);
        }
        return res;
    }
    
    // fill the matricies and vectors that we need
    // i.e., create sumYY, sumPP, and sumPY
    public void computeSums() {
        // first compute sumYY
        sumYY = 0.0;
        for (double[] pair : legendreData) {
            sumYY += pair[1] * pair[1];
        }
        
        // now compute sumPP
        sumPP = new double[dim][dim];
        for (int o1 = 0; o1 < dim; o1++) {
            for (int o2 = 0; o2 < dim; o2++) {
                sumPP[o1][o2] = computePTwiddle(o1, o2); 
            }
        }
        
        // now compute sumPY
        sumPY = new double[dim];
        for (int o = 0; o < dim; o++) {
            sumPY[o] = computeYTwiddle(o);
        }
    }
    
    // get the coefficients using cholesky decomp
    public void solveFit() {
        this.cholesky = Cholesky.cholesky(sumPP);
        this.choleskyP = Cholesky.extractDiagonal(cholesky);
        this.choleskyB = Cholesky.choleskySolver(cholesky, choleskyP, sumPY);
    }
    
    // compute the value of the fit at a given point
    public double fitValue(double z) {
 	double sum = 0;
	for (int i = 0; i < dim; i++)
		sum += choleskyB[i] * LegendrePolynomials.P(i, z);
	return sum;
    }
    
    // get the error of the fit at a given point
    public double fitError(double z) {
        double[] pvec = new double[dim];
	for (int i = 0; i < dim; i++) {
            pvec[i] = LegendrePolynomials.P(i, z);
        }
	double[] pvec2 = pvec;
        
	Cholesky.choleskySolver(cholesky, choleskyP, pvec2);
	return Math.sqrt(variance * Cholesky.dotProduct(pvec, pvec2));
    }
}
