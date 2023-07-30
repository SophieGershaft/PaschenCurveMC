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
public class SphereInCylinder implements IGeometry {
    double r1;
    double r2;
    double a;
    double b;
    double V;
    Random random;

    // initialize these settings
    double lambda;
    double lambda_i;
    int count;
    double Ui;
    double delta_t;
    double Nc;
    double Ni;

    public SphereInCylinder(Random random) {
        // compute a and b
        this.r1 = SettingsSC.getInstance().getRSphere();
        this.r2 = SettingsSC.getInstance().getRCylinder();
        this.V = SettingsSC.getInstance().getV();
        this.random = random;
        // initialize things
        this.lambda = SettingsSC.getInstance().getLambda();
        this.lambda_i = SettingsSC.getInstance().getLambdaI();
        this.count = SettingsSC.getInstance().getCount();
        this.Ui = SettingsSC.getInstance().getUI();

        this.delta_t = SettingsSC.getInstance().getDeltaT();
        this.Nc = SettingsSC.getInstance().getNc();
        this.Ni = SettingsSC.getInstance().getNi();
    }

    @Override
    public boolean isCathode(Vector r) {
        // if magnitude of r is < radius to cathode --> on or inside cathode
        return (r.getNorm() < r1);
    }

    // FIX THIS TO WORK WITH CYLINDER ANODE
    @Override
    public boolean isAnode(Vector r) {
        // if magntiude of r is >= radius to anode --> on or outside anode
        return (r.getNorm() >= r2);
    }

    @Override
    public Vector getEfield(Vector pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        
        Vector Efield = new Vector(0, 0, 0);
        double[] res = Interpolation.getEFieldSC(this.V, x, y, z);
        Efield.x = res[0];
        Efield.y = res[1];
        Efield.z = res[2];
        return Efield;
    }

    @Override
    public double getPotential(Vector pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        
        double potential = Interpolation.getPotentialSC(this.V, x, y, z);
        return potential;
    }

    @Override
    public Vector cathodeStart() {
        // random point on sphere
        double u = random.nextDouble();
        double v = random.nextDouble();
        double phi = u * 2 * Math.PI;
        // 2*v - 1 is cos(theta)
        double theta = Math.acos(2 * v - 1);
        double r1_a = r1 + 1e-9 * (r2 - r1);

        double x = r1_a * Math.sin(theta) * Math.cos(phi);
//        double y = r1_a * Math.sin(theta) * Math.sin(phi);
//        double z = r1_a * Math.cos(theta);
//        Vector start = new Vector(x, y, z);

        Vector start = new Vector(r1_a, 0.0, 0.0); // --> same thing as above but runs faster (only in SS bc completely symmetrical)
        // result should be vector w/ properly randomly selected direction
        // point should lie on surface of sphere w/ radius r
        return start;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public double getDeltaT() {
        return delta_t;
    }

    @Override
    public double getLambda() {
        return lambda;
    }

    @Override
    public double getLambdaI() {
        return lambda_i;
    }

    @Override
    public double getUI() {
        return Ui;
    }

    @Override
    public double getNc() {
        return Nc;
    }

    @Override
    public double getNi() {
        return Ni;
    }
    
}