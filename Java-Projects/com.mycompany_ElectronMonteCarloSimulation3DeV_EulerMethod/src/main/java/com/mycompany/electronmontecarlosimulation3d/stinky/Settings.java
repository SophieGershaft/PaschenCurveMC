///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mycompany.electronmontecarlosimulation3d.stinky;
//
///**
// *
// * @author sgershaft
// */
//public class Settings {
//    // make lambda_c, which is the mean free path
//    // make anode_pos, distance from cathode to anode
//    // make voltage, which is the potential difference from cathode to anode
//    // make lambda_i, which is the ionizaiton distance
//    // make U_i, which is the ionization energy
//    // make reps, which is the number of times the simulation should be repeated
//    // (aka number of source electrons to follow)
//    // make tolerance for calculating time of collision w/ Newton's Method
//    public double lambda_c; // in m
//    public double anode_pos; // in m 
//    public double voltage; // in volts
//    public double lambda_i; // in m
//    public double U_i; // in electronvolts
//    public double reps; // unitless
//    public double tolerance; // in m
//    public double Ni;
//    public double Nc;
//    // E field tells volts/meter or Newtons/Coulomb 
//    // TODO:
//    // make it a static class that can be referred to instead of passing them
//    // into the constructor 
//    // --> need a reset method 
//    
//    // know lambda_i and anode_pos --> know Ni
//    // know lambda_c and anode_pos --> know Nc
//    // --> UH OH: ^ isn't true bc now, there's a transverse component
//    // --> can't say anode_pos/lambda_c = Nc bc it doesn't; anode_pos is only x direction
//    // can plot similar graph 
//    
//    // --> --> Nc = 8
//    
//    // note: i never actually use lambda_i currently
//    
//    // to start: 
//    // assume U_i = 15 eV 
//    // start at 1000 volts w/ anode_pos = 8 cm = 0.08 m
//}
