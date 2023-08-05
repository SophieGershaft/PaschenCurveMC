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
public class SphereInSphere implements IGeometry {

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

    public SphereInSphere(Random random) {
        // compute a and b
        this.r1 = SettingsSS.getInstance().getRInner();
        this.r2 = SettingsSS.getInstance().getROuter();
        this.b = r1 / (r2 - r1);
        this.a = -r2 * b;
        this.V = SettingsSS.getInstance().getV();
        this.random = random;
        // a/r2 + b = 0 --> anode
        // a/r1 + b = b(1 - r2/r1) = b(r1-r2)/r1 = -1 --> cathode
        // initialize things
        this.lambda = SettingsSS.getInstance().getLambda();
        this.lambda_i = SettingsSS.getInstance().getLambdaI();
        this.count = SettingsSS.getInstance().getCount();
        this.Ui = SettingsSS.getInstance().getUI();

        this.delta_t = SettingsSS.getInstance().getDeltaT();
        this.Nc = SettingsSS.getInstance().getNc();
        this.Ni = SettingsSS.getInstance().getNi();
    }

    @Override
    public boolean isCathode(Vector r) {
        // if magnitude of r is < radius to cathode --> on or inside cathode
        return (r.getNorm() < r1);
    }

    // FIX THIS TO WORK WITH CYLINDER
    @Override
    public boolean isAnode(Vector r) {
        // if magntiude of r is >= radius to anode --> on or outside anode
        return (r.getNorm() >= r2);
    }

    // FUTURE: RETHINK WHETHER I CAN SPEED IT UP BY NOT CALLING THINGS TWICE
    @Override
    public Vector getEfield(Vector pos) {
        double rSquare = pos.Square();
        double r = pos.getNorm();
        // TODO: SHOULD USE ISCATHODE OR ISANODE
        if (r < r1 || r >= r2) {
            // inside cathode or outside anode (or inside the metal) --> Efield = 0
            Vector Efield = new Vector(0, 0, 0);
            return Efield;
        }
        // unit vector r^
        Vector rHat = pos.getUnitVector();
        // calculate Efield in sphere w/ Gauss's Law / Coulomb's Law
        Vector Efield = rHat.multiplyByScalar((V * a) / rSquare);
        return Efield;
    }

    @Override
    public double getPotential(Vector pos) {
        double radius = pos.getNorm();
        // TODO: SHOULD USE ISCATHODE OR ISANODE !
        if (radius < r1) {
            return -V;
        }
        if (radius >= r2) {
            return 0.0;
        }
        return V * (a / radius + b);
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
