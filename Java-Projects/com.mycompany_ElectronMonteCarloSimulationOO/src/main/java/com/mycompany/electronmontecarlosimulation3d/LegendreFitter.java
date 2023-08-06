package com.mycompany.electronmontecarlosimulation3d;

import java.util.ArrayList;

/**
 *
 * @author wbowers
 */
public class LegendreFitter {
    
    // using even orders from 0-10, so 6 dimensions
    public final int dim = 6;
    public ArrayList<double[]> legendreData;
    public double[][] sumPP;
    public double[] sumPY;
    public double sumYY;
    public double[][] cholesky;
    public double[] choleskyP;
    public double[] choleskyB;
    public double variance;
    
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
    
    public double computePTwiddle(int o1, int o2) {
        double res = 0.0;
        for (double[] pair : legendreData) {
            double xi = pair[0];
            res += (LegendrePolynomials.P(o1, xi) * LegendrePolynomials.P(o2, xi));
        }
        return res;
    }
    
    public double computeYTwiddle(int o) {
        double res = 0.0;
        for (double[] pair : legendreData) {
            double xi = pair[0];
            double yi = pair[1];
            res += (LegendrePolynomials.P(o, xi) * yi);
        }
        return res;
    }
    
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
    
    public void solveFit() {
        this.cholesky = Cholesky.cholesky(sumPP);
        this.choleskyP = Cholesky.extractDiagonal(cholesky);
        this.choleskyB = Cholesky.choleskySolver(cholesky, choleskyP, sumPY);
    }
    
        
    public double fitValue(double z) {
 	double sum = 0;
	for (int i = 0; i < dim; i++)
		sum += choleskyB[i] * LegendrePolynomials.P(i, z);
	return sum;
    }
    
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
