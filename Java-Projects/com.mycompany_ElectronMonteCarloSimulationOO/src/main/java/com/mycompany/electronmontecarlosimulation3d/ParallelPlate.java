/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author sgershaft
 */
public class ParallelPlate implements IGeometry {

    double d;
    double V;
    Random random;
    
    // initialize these settings
    double lambda;
    double lambda_i;
    int count;
    double Ui;
    
    double Nc;
    double Ni;
    double delta_t;

    public ParallelPlate(Random random) throws IOException {
        this.d = SettingsPP.getInstance().getD();
        if (this.d < 1e-10) {
            throw new IOException("Wrong D!");
        }
        this.V = SettingsPP.getInstance().getVoltage();
        this.random = random;
    }

    // check whether electron is inside or on the cathode
    @Override
    public boolean isCathode(Vector r) {
        // if x component of r is at cathode (distance is 0)
        return (r.x <= 0);
    }

    @Override
    public boolean isAnode(Vector r) {
        // if x component of r is >= distance to anode --> on or outside anode
        return (r.x >= d);
    }

    @Override
    public Vector getEfield(Vector pos) {
        Vector Efield = new Vector(-V / d, 0, 0);
        return Efield;
    }

    @Override
    public double getPotential(Vector pos) {
        double potential = (pos.x * V) / d;
        return potential;
    }

    @Override
    public Vector cathodeStart() {
        // it doesn't matter where it starts on the plate because only the x-direction really matters
        Vector start = new Vector(0, 0, 0);
        return start;
    }
    
    @Override
    public int getCount() {
        return count;
    }
    
    @Override
    public double getDeltaT() {
        return delta_t;
    }

    @Override
    public void initialize() {
        this.lambda = SettingsPP.getInstance().getLambda();
        this.lambda_i = SettingsPP.getInstance().getLambdaI();
        this.count = SettingsPP.getInstance().getCount();
        this.Ui = SettingsPP.getInstance().getUI();

        // FOR DEBUGGING PURPOSES:
        this.Nc = SettingsPP.getInstance().getNc();
        this.Ni = SettingsPP.getInstance().getNi();
        this.delta_t = SettingsPP.getInstance().getDeltaT();
    }
}
