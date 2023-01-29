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
public interface IGeometry {
    
    // i feel like this is wrong
    double lambda = 0;
    double lambda_i = 0;
    double Ui = 0;
    
    double Nc = 0;
    double Ni = 0;
 
    boolean isCathode(Vector r);
    
    boolean isAnode(Vector r); 
    
    Vector getEfield(Vector pos); 
    
    double getPotential(Vector pos);
    
    Vector cathodeStart();
    
    void initialize();
    
    int getCount();
    
    double getDeltaT();
}
