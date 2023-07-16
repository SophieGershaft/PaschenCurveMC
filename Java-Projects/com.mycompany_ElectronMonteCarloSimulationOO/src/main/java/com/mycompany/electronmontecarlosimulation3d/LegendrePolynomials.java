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
    public static double P(int n, double x) {
        // P0(x) and P1(x) are given
        if (n == 0) {
            return 1;
        }
        if (n == 1) {
            return x;
        }
        // check for invalid input
        if (n < 0) {
            return Double.NaN;
        }
        // https://en.wikipedia.org/wiki/Legendre_polynomials#Definition_via_generating_function
        // note that n is shifted down by 1 
        double p = ((2 * n - 1) * x * P(n - 1, x) - n * P(n - 2, x)) / n;
        return p;
    }
}
