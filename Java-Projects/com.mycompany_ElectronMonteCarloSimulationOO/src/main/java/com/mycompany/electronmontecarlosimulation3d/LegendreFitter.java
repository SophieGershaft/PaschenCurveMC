package com.mycompany.electronmontecarlosimulation3d;

/**
 *
 * @author wbowers
 */
public class LegendreFitter {
    
    // using even orders from 0-10, so 6 dimensions
    public static final int dim = 6;
    
    // given the cholesky decomposition, coefficients, and variance of the fit,
    // compute the maximum ions across all possible cos(theta) with error
    public double[] findMax(double[] cholesky, double[] choleskyP, double[] choleskyB, double variance) {
     
        double bestZ = 0.0;
	double bestIons = LegendreFitter.fitValue(choleskyB, 0.0);
	double bestError = LegendreFitter.fitError(cholesky, choleskyP, choleskyB, 0.0, variance);
        
        for (int i = 1; i <= 100; i++) {
            double z = i / 100.0;
            double val = LegendreFitter.fitValue(choleskyB, z);
            if (val > bestIons) {
                bestZ = z;
                bestIons = val;
                bestError = LegendreFitter.fitError(cholesky, choleskyP, choleskyB, z, variance);
            }
        }
        double[] res = { bestZ, bestIons, bestError };
        return res;
    }
    
        
    public static double fitValue(double[] choleskyB, double z) {
 	double sum = 0;
	for (int i = 0; i < dim; i++)
		sum += choleskyB[i] * LegendrePolynomials.P(i, z);
	return sum;
    }
    
    public static double fitError(double[] cholesky, double[] choleskyP, 
            double[] choleskyB, double z, double variance) {
        
        double[] pvec = new double[dim];
	for (int i = 0; i < dim; i++) {
            pvec[i] = LegendrePolynomials.P(i, z);
        }
	double[] pvec2 = pvec;
        
	Cholesky.CholeskySolver(cholesky, choleskyP, pvec2);
	return Math.sqrt(variance * LegendreFitter.dotProduct(pvec, pvec2));
    }
    
    
    // utility functions

    public static double dotProduct(double[] array1, double[] array2) {
        double result = 0.0;
        for (int i = 0; i < array1.length; i++) {
            result += array1[i] * array2[i];
        }
        return result;
    }
    
    public static double computeVariance(double sumYY, double[] choleskyB, 
            double[] sumPy, int count, int dim) {
        
        return (sumYY - LegendreFitter.dotProduct(choleskyB, sumPy)) / (count - dim);
    }
    
}
