///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mycompany.electronmontecarlosimulation3d.stinky;
//
//import com.mycompany.electronmontecarlosimulation3d.Distributions;
//import org.apache.commons.math3.analysis.function.Asinh;
//
///**
// *
// * @author sgershaft
// */
//// TODO: make settings static
//// only carry what is necessary in the electron object instance (what is specific to this specific instance)
//
//public class Electron {
//
//    int collisionCount;
//    int ionizationCount;
//    double x;
//    double v_x;
//    double v_w;
//    double a; // a = (qE)/m TODO!!!
//    final double t_guess = 1e-8;
//    Settings settings;
//    // define constant a via charge and mass of electron
//    final static double q = 1.60217662e-19;
//    final static double m = 9.10938356e-31;
//    final static double eV = 1.602176565e-19; // 1 eV = this many Joules
//    // V/d = E
//    // Voltage comes from settings, d is distance from cathode to anode
//    // U_I is ionization voltage 
//    // lambda is MFP, is d/Nc (distance over num of collision lengths)
//    // d/Nc always should be 1 so we can compare the results to the 1D simulation (w/o having diff units)
//
//    // always start a new electron w/ a velocty of 0 
//    // 1st electron starts w/o velocity; new electron from ionization event also starts w/o velocity
//    public Electron(double x, Settings settings) {
//        this.x = x;
//        this.settings = settings;
//        // electric field and acceleration
//        double E = settings.voltage / settings.anode_pos;
//        this.a = (this.q * E) / this.m;
//    }
//
//    // TODO:
//    // calculate everything into local variables 
//    // (instead of mixing where I update and where i use local variables)
//    // make update() method where i put local variables back into the object
//    
//    // IMPORTANT !!!!!!!11
//    // need to track y, z
//    public boolean nextStep() {
//        // (time-independent will be draw S, calculate w, calculate x at collision)
//        // update x, update v_x, update v_w
//        // draw S, calculate T at collision, calculate X at collision
//        // calculate v_x at collision, v_c, and K_c
//        double s = drawS();
//        double t_c = this.calculateT(s, this.t_guess);
//        double x0 = this.x;
//        // x pos of collision
//        double x_c = calculateX(t_c); 
//        
//        // update electron's x
//        this.x = x_c;
//        if (reachedAnode(x_c)) {
//            // if reached anode, electron is finished
//            // if not finished, collision happened
//            return false;
//        }
//        collisionCount++;
//        double v_x = calculateV_x(t_c); // double check this
//        double v_c = calculateV_c(t_c);
//        double K_c = calculateK_c(v_c);
//        boolean isIonizing = isIonizing(K_c);
//        if (isIonizing) {
//            ionizationCount++;
//        }
//        double K_f = calculateK_f(isIonizing, K_c);
//        double v_f = calculateV_f(K_f);
//        double cosTheta = drawCosTheta();
//        // update electron's v_x, and v_w
//        this.v_x = calculateV_xf(v_f, cosTheta);
//        this.v_w = calculateV_wf(v_f, cosTheta);
//        return isIonizing;
//    }
//
//    boolean reachedAnode(double x) {
//        if (x >= settings.anode_pos) {
//            return true;
//        } // else
//        return false;
//    }
//
//    private double drawS() {
//        // s is the distance to next collision
////        return Distributions.cdfExponential(settings.lambda_c);
//    }
//
//    private double calculateS_of_t(double t) {
//        // calculate distance S from an input t (guess time of collision)
//        // use solution of s
//        Asinh asinh = new Asinh();
//        double atvx = (a * t + v_x);
//        double vw2 = v_w * v_w;
//        double s;
//        if (v_w <= 1e-6) {
//            s = (atvx * Math.abs(atvx) - (this.v_x * Math.abs(this.v_x))) / (2.0*this.a);
//        } else {
//            s = (1.0 / (2.0 * a)) * (atvx * Math.sqrt(vw2 + atvx * atvx)
//                    - v_x * Math.sqrt(vw2 + v_x * v_x)
//                    + vw2 * (asinh.value(atvx / v_w) - asinh.value(v_x / v_w)));
//        //  double s = (1/(2*a))* ((a*t + v_x)*Math.sqrt(v_w*v_w + (v_x + a*t)*(v_x + a*t)) - v_x*Math.sqrt(v_w*v_w + v_x*v_x) 
//        //          + v_w*v_w* (asinh.value((a*t+v_x)/v_w) - asinh.value(v_x/v_w))); 
//        }
//        return s;
//    }
//
//    // should be interior of integral
//    private double calculateV_c(double t_c) {
//        // calculates velocity before collision
//        // v_x and v_w are velocities from after previous collision
//        // v_c is value from integral
//        return Math.sqrt(this.v_w * this.v_w + (this.v_x + this.a * t_c) * (this.v_x + this.a * t_c));
//    }
//
//    private double calculateT(double s, double t_guess) {
//        // calculate time at next collision using kinematics
//        // assuming the Newton-Raphson solver works, I won't need the loop
//        // for now, i'm doing it "by hand":
//        double t = t_guess;
//        while (true) {
//            double guessDistance = calculateS_of_t(t);
//            if (Math.abs(s - guessDistance) < settings.tolerance) {
//                // it's close enough now
//                return t;
//            } else {
//                // change t using Newton's Method
//                // newton's method is:
//                // x_n+1 = x0 - f(x_n)/f'(x_n)
//                // so:
//                // t_n+1 = t0 - s(t_n)/v(t_n)
//                t = t - (guessDistance - s) / calculateV_c(t);
//                // QUESTION: What do I do if V_c = 0? Division by 0 bad. This happens if initial t = 0
//                // Should I make initial t other than 0?
//            }
//        }
//    }
//
//    private double calculateX(double t) {
//        // takes x_previous, t, and v_x
//        // calculate x from kinematics equation
//        double x = this.x + this.v_x * t + 0.5 * this.a * t * t;
//        return x;
//    }
//
//    private double calculateV_x(double t) {
//        // calculates v_x after collision (not updating v_x, just calculating)
//        double v_x = this.v_x + this.a * t;
//        return v_x;
//    }
//
//    private double calculateK_c(double v_c) {
//        // calculate kinetic energy right before the collision
//        return this.m * (v_c * v_c) / 2.0;
//    }
//
//    private boolean isIonizing(double K_c) {
//        // convert ionization energy to Joules from eV for energy calculation
//        if (K_c >= settings.U_i * this.eV) {
//            // ionizing collision
//            return true;
//        } else { // K_c < U_i
//            // not ionizing collision
//            return false;
//        }
//    }
//
//    private double calculateK_f(boolean isIonizing, double K_c) {
//        if (isIonizing) {
//            // K_f = K_c - U_i
//            return K_c - settings.U_i  * this.eV;
//        } else {
//            // K_f = K_c bc no energy is lost 
//            return K_c;
//        }
//    }
//
//    private double calculateV_f(double K_f) {
//        // calculates v_f, velocity after collision
//        return Math.sqrt(2.0 * K_f / this.m);
//    }
//
//    private double drawCosTheta() {
//        double R = Math.random();
//        double cosTheta = 1.0 - 2.0 * R;
//        return cosTheta;
//    }
//
//    private double calculateV_xf(double v_f, double cosTheta) {
//        // v_xf = v_f * cosTheta
//        return v_f * cosTheta;
//    }
//
//    private double calculateV_wf(double v_f, double cosTheta) {
//        // v_xf = v_f * sinTheta
//        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
//        return v_f * sinTheta;
//    }
//
//}
//
