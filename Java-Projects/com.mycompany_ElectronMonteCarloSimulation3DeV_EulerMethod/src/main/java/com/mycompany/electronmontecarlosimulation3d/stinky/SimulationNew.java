///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mycompany.electronmontecarlosimulation3d.stinky;
//
//import com.mycompany.electronmontecarlosimulation3d.stinky.StatsNew;
//import com.mycompany.electronmontecarlosimulation3d.stinky.ElectronNew;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Settings;
//import java.util.LinkedList;
//import java.util.Queue;
//import org.apache.commons.lang3.time.StopWatch;
//
///**
// *
// * @author sgershaft
// */
//public class SimulationNew {
//
//    private Settings settings;
//    private Queue<ElectronNew> queue;
//    private StopWatch stopwatch;
//    private StatsNew stats;
//
//    public SimulationNew(Settings settings) {
//        this.settings = settings;
//        this.queue = new LinkedList<ElectronNew>();
//        this.stopwatch = new StopWatch();
//        this.stats = new StatsNew(settings);
//    }
//
//    public StatsNew run() {
//        stopwatch.start();
//        for (int i = 0; i < settings.reps; i++) {
//            // create single electron at x = 0 and add to queue
//            ElectronNew electron = new ElectronNew(0.0, 0.0, 0.0, settings);
//            queue.add(electron);
//
//            // Simulation Loop:
//            while (queue.size() > 0) {
//                // taking an electron to follow
//                ElectronNew currElectron = queue.remove();
//                do {
//                    // if ionization event occurred, put new Electron in queue
//                    if (currElectron.nextStepOne()) {
//                        ElectronNew newElectron = new ElectronNew(currElectron.x, currElectron.y, currElectron.z, settings);
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
