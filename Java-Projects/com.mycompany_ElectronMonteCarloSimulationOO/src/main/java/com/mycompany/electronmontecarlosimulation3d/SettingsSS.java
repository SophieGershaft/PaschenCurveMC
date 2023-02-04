/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.mycompany.electronmontecarlosimulation3d.stinky.SettingsPP;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author sgershaft
 */
public class SettingsSS {
    // singleton class --> can only have one instance of settings
    // velocity should be beta, in fraction of speed of light
    // to convert to SI units: multiply by C
    // x is m, dx/dt is unitless 
    // ONLY UNITS ARE: volts, eV, and m (if we use beta for velocity)
    // 1/2 m v^2 --> eV is unit

    public static SettingsSS instance = null;

    private double r1; // inner radius in cm 
    private double r2; // outer radius in cm
    public double Ui; // in electronvolts
    private double Nc;
    private double Ni;
    private int count; // unitless, num of electrons to launch
    private double delta_t;

    private SettingsSS() {
    }

    public static SettingsSS getInstance() {
        if (instance == null) {
            instance = new SettingsSS();
        }
        return instance;
    }

    public double getRInner() {
        return r1;
    }

    public double getROuter() {
        return r2;
    }

    public int getCount() {
        return count;
    }
    
    public double getDeltaT() {
        return delta_t;
    }

    public double getNc() {
        return Nc;
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
        double lambda = (r2 - r1) / Nc;
        return lambda;
    }

    public double getLambdaI() {
        double lambda_i = (r2 - r1) / Ni;
        return lambda_i;
    }

    public void setRInner(double value) {
        this.r1 = value;
    }

    public void setROuter(double value) {
        this.r2 = value;
    }

    public void setNc(double value) {
        this.Nc = value;
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
        try ( BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // deserialization (reading from file into objects)
            instance = gson.fromJson(reader, SettingsSS.class);
        }
    }
}

/*

    SYSTEM OF UNITS

    3 fundamental variables: d (in meters), V (voltage in system, in volts), Nc, and Ni
    derivative quantities: lambda_c = d/Nc, lambda_i = d/Ni, E = V/d, Ui = e*E*lambda_i (an electric potential energy in eV)

    

 */
