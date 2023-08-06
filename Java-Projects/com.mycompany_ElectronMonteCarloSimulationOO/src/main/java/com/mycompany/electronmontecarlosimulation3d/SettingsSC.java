/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author sgershaft
 */
public class SettingsSC {
    // singleton class --> can only have one instance of settings
    // velocity should be beta, in fraction of speed of light
    // to convert to SI units: multiply by C
    // x is m, dx/dt is unitless 
    // ONLY UNITS ARE: volts, eV, and m (if we use beta for velocity)
    // 1/2 m v^2 --> eV is unit

    public static SettingsSC instance = null;

    private double r1; // inner radius in cm (sphere)
    private double r2; // outer radius in cm (cylinder)
    private double h; // height of cylinder in cm
    private double Ui; // in electronvolts
    private double lambda; // MFP, a known quantity only for SC
    private double Ni; 
    private int count; // unitless, num of electrons to launch
    private double delta_t;

    private SettingsSC() {
    }

    public static SettingsSC getInstance() {
        if (instance == null) {
            instance = new SettingsSC();
        }
        return instance;
    }

    public double getRSphere() {
        return r1;
    }

    public double getRCylinder() {
        return r2;
    }
    
    public double getH() {
        return h;
    }

    public int getCount() {
        return count;
    }

    public double getDeltaT() {
        return delta_t;
    }

    public double getNi() {
        return Ni;
    }

    public double getV() {
        double V = Ni * Ui;
        return V;
    }

    public double getUI() {
        return Ui;
    }

    public double getLambda() {
        return lambda;
    }
    
    public void setLambda(double value) {
        this.lambda = value;
    }

    public void setRInner(double value) {
        this.r1 = value;
    }

    public void setROuter(double value) {
        this.r2 = value;
    }
    
    public void setH(double value) {
        this.h = value;
    }

    public void setNi(double value) {
        this.Ni = value;
    }

    public void setCount(int value) {
        this.count = value;
    }

    // read settings from json 
    public static void fromJSON(String fileName) throws IOException {
        // get json gson 
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Automatically close reader so that files don't stay open if broken
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // deserialization (reading from file into objects)
            instance = gson.fromJson(reader, SettingsSC.class);
        }
    }
}