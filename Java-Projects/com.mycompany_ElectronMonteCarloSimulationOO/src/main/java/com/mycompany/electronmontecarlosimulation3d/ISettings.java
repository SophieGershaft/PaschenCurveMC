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
public interface ISettings {

    public double getVoltage();

    public double getLambda();

    public double getLambdaI();

    public double getCount();
    
    public void setCount(double value);

    public double getDeltaT();

    public double getNi();
    
    public void setNi(double value);

    public double getNc();
    
    public void setNc(double value);

    public double getUI();
    
    public void setUI(double value);

    public double getAx();
 
}
