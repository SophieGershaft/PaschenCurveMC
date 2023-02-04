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
    
    boolean isCathode(Vector r);
    
    boolean isAnode(Vector r); 
    
    Vector getEfield(Vector pos); 
    
    double getPotential(Vector pos);
    
    Vector cathodeStart();
    
    int getCount();
    
    double getDeltaT();
    
    double getLambda();
    
    double getLambdaI();
    
    double getUI();
}
