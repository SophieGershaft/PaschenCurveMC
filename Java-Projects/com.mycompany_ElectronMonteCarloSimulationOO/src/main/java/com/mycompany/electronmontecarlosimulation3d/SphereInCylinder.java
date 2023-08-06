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
    double h;
    double V;
    Random random;

    // initialize these settings
    double lambda;
    int count;
    double Ui;
    double delta_t;
    double Ni;

    public SphereInCylinder(Random random) {
        // initialize things
        this.r1 = SettingsSC.getInstance().getRSphere();
        this.r2 = SettingsSC.getInstance().getRCylinder();
        this.h = SettingsSC.getInstance().getH();
        this.V = SettingsSC.getInstance().getV();
        this.random = random;
        this.lambda = SettingsSC.getInstance().getLambda();
        this.count = SettingsSC.getInstance().getCount();
        this.Ui = SettingsSC.getInstance().getUI();
        this.delta_t = SettingsSC.getInstance().getDeltaT();
        this.Ni = SettingsSC.getInstance().getNi();
    }

    @Override
    public boolean isCathode(Vector r) {
        // if magnitude of r is < radius to cathode --> on or inside cathode
        return (r.getNorm() < r1);
    }

    // TEST THIS TO MAKE SURE IT WORKS
    @Override
    public boolean isAnode(Vector r) {
        // if magntiude of sqrt(x^2 + y^2) is >= cylinder radius to anode --> on or outside anode
        // if z is <= -h/2 or >= h/2  --> on or outside anode
        return ((r.x * r.x)+(r.y * r.y) >= r2*r2 || (r.z <= -h/2) || (r.z >= h/2));
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
        
        double r1a = r1 + 1e-8;
        

        double x = r1a * Math.sin(theta) * Math.cos(phi);
        double y = r1a * Math.sin(theta) * Math.sin(phi);
        double z = r1a * Math.cos(theta);
        Vector start = new Vector(x, y, z);

//        Vector start = new Vector(r1_a, 0.0, 0.0); // --> same thing as above but runs faster (only in SS bc completely symmetrical)
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
        // THIS IS BAD --> throw an exception
        return 0.0;
    }

    @Override
    public double getUI() {
        return Ui;
    }

    @Override
    public double getNc() {
        // THIS IS BAD --> throw an exception
        return 0.0;
    }

    @Override
    public double getNi() {
        return Ni;
    }

}
