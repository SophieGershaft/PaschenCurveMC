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
public class LegendrePolynomials {

    // finds the Legendre polynomials for degree (or order?) 2, 4, 6, 8, 10 using the Bonnet recursion formula
    public static double P(int n, double z) {
        // P0(z) and P1(z) are given
        if (n == 0) {
            return 1;
        }
        if (n == 1) {
            return z;
        }
        // check for invalid input
        if (n < 0) {
            return Double.NaN;
        }
        // https://en.wikipedia.org/wiki/Legendre_polynomials#Definition_via_generating_function
        // note that n is shifted down by 1 
        double p = ((2 * n - 1) * z * P(n - 1, z) - n * P(n - 2, z)) / n;
        return p;
    }
}
