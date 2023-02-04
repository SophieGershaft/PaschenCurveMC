///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mycompany.electronmontecarlosimulation3d.stinky;
//
//import com.mycompany.electronmontecarlosimulation3d.stinky.Electron;
//import java.util.Collection;
//
///**
// *
// * @author sgershaft
// */
//public class Stats {
//
//    // collect important stats about simulation
//    private double sum_col;
//    private double sum_ion;
//    private double sum_electrons;
//    private double mean_electrons; // avg per simulation (per rep)
//    private double mean_col;
//    private double mean_ion;
//    private double stdev_col;
//    private double stdev_ion;
//    private Settings settings;
//
//    public Stats(Settings settings) {
//        this.settings = settings;
//    }
//    
//    
//    // need to do this for each electron
//    public void add(Electron e) {
//        // instead of iterating over a collection of electrons, this gets one at a time
//        sum_col += e.collisionCount;
//        sum_ion += e.ionizationCount;
//        sum_electrons += 1;
//    }
//    
//    public void computeOne() {
//        mean_col = sum_col / settings.reps;
//        mean_ion = sum_ion / settings.reps;
//        mean_electrons = sum_electrons / settings.reps;
//        
//        // Note: not implementing stdev yet bc it's too much work and we don't use it
//        // but that would be a TODO
//    }
//
//    public double getSumCol() {
//        return sum_col;
//    }
//
//    public double getSumIon() {
//        return sum_ion;
//    }
//
////    public double getAverageIon() {
////        average_ion = sum_ion / UnitlessSettings.reps;
////        return average_ion;
////    }
//    
//    public double getMeanCol() {
//        return mean_col;
//    }
//
//    public double getMeanIon() {
//        return mean_ion;
//    }
//
//    public double getStdDevCol() {
//        return stdev_col;
//    }
//
//    public double getStdDevIon() {
//        return stdev_ion;
//    }
//
//    public double getSumElectron(){
//        return sum_electrons;
//    }
//}