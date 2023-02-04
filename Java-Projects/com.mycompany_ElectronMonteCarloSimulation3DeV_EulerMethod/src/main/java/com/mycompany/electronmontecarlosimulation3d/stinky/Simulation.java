///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mycompany.electronmontecarlosimulation3d.stinky;
//
//import com.mycompany.electronmontecarlosimulation3d.stinky.Stats;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Electron;
//import java.util.LinkedList;
//import java.util.Queue;
//import org.apache.commons.lang3.time.StopWatch;
//
///**
// *
// * @author sgershaft
// */
//public class Simulation {
//
//    private Settings settings;
//    private Queue<Electron> queue;
//    private StopWatch stopwatch;
//    private Stats stats;
//    
//
//    public Simulation(Settings settings) {
//        this.settings = settings;
//        this.queue = new LinkedList<Electron>();
//        this.stopwatch = new StopWatch();
//        this.stats = new Stats(settings);
//    }
//
//    public Stats run() {
//        stopwatch.start();
//        for (int i = 0; i < settings.reps; i++) {
//            // create single electron at x = 0 and add to queue
//            Electron electron = new Electron(0.0, settings);
//            queue.add(electron);
//
//            // Simulation Loop:
//            while (queue.size() > 0) {
//                // taking an electron to follow
//                Electron currElectron = queue.remove();
//                do {
//                    // if ionization event occurred, put new Electron in queue
//                    if (currElectron.nextStep()) {
//                        Electron newElectron = new Electron(currElectron.x, settings);
//                        queue.add(newElectron);
//                    }
//                } while (!currElectron.reachedAnode(currElectron.x));
//                // when reaches anode, done with electron
//
//                // get "statistics" of electron (collision counts, ionization counts, etc.)
//                stats.add(currElectron);
//            }
//        }
//        stopwatch.stop();
//        stats.computeOne();
//        System.out.println("Elapsed: " + stopwatch.formatTime());
//        return stats;
//    }
//}
