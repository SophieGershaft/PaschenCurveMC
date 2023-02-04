//@@ -0,0 +1,181 @@
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

//import com.mycompany.electronmontecarlosimulation3d.stinky.SettingsPP;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Simulation;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Stats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.mycompany.electronmontecarlosimulation3d.stinky.SettingsPP;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Simulation;
//import com.mycompany.electronmontecarlosimulation3d.stinky.Stats;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author sgershaft
 */
public class Main {
    // TODO: !!!!!!!!!!!!!! MAKE IT WORK FOR BOTH PP AND SS

    // FOR DEBUGGING:
    public static Random random = new Random(12);
    public static ArrayList<Double> randomNums = new ArrayList<Double>();

    public static void main(String[] args) throws IOException {
        System.out.println("Parameters: " + String.join(",", args));
        // To support multiple settings --> use command-line arguments.
        CommandLine cmdLine = parseArguments(args);
        
        String resource;
        IGeometry geometry = null;
        if (cmdLine.hasOption("input")) {
            // input is a required parameter - input is settings name
            resource = cmdLine.getOptionValue("input");
            // Get "resource" settings file name
            // gets the settings.json filepath (googled how to do)
            String fileName = Main.class.getClassLoader()
                    .getResource(resource).getFile();
            // spaces had a weird error so had to replace %20 with spaces for it to work
            File file = new File(fileName.replace("%20", " "));
//            SettingsPP.fromJSON(file.getAbsolutePath());
            
            // get geometry
            geometry = createGeometry(file.getAbsolutePath(), random);
        }

        // TODO: print all settings + include in results file 
        // TEMPORARY SOLUTION : still need factory function to make it run w/ both PP and SS
//        IGeometry geometry = new ParallelPlate(random);
        Simulation sim = new Simulation(geometry, random);

        MeanAndError result = sim.run(false, 0.8); // printThings, forwardScatter

//        runForRatioPP(1.0, 0.2, 14.0, 0.8, 2500);
        System.out.println(result);
//        randomSeedTester();

        ArrayList<MeanAndError> theResults = new ArrayList<MeanAndError>();
        theResults.add(result);
        writeJSON(theResults, "results.json");

//        findPointOnPaschenCurveLitePP(110, 27.5, 28, 11, random);
    }

    // create geometry based on json input file
    public static IGeometry createGeometry(String fileName, Random random) {
        try {
            // parsing JSON file: can it be parsed as parallel plate?
            SettingsPP.fromJSON(fileName);
            // create and return ParallelPlate geometry
            IGeometry geometry = new ParallelPlate(random);
            return geometry;
        } catch (IOException e) {
            try {
                // can it be parsed as a sphere in sphere?
                SettingsSS.fromJSON(fileName);
                // create and return SphereInSphere geometry
                IGeometry geometry = new SphereInSphere(random);
                // initialize geometry with values from settings
                return geometry;
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return null;
    }

    // NEW!!!
    // lite version = it prints things and i manually pick where to zoom in and re-run
    // note: forward scattering value is hardcoded in here
    public static void findPointOnPaschenCurveLitePP(double Nc, double NiStart, double NiEnd, int numSteps) throws IOException {
        ArrayList<MeanAndError> results = new ArrayList<MeanAndError>();
        double increment = (NiEnd - NiStart) / numSteps;
        for (double Ni = NiStart; Ni <= NiEnd; Ni += increment) {
            SettingsPP.getInstance().setNc(Nc);
            SettingsPP.getInstance().setNi(Ni);

            // TEMPORARY SOLUTION : still need factory function to make it run w/ both PP and SS
            IGeometry geometry = new ParallelPlate(random);
            Simulation sim = new Simulation(geometry, random);

            MeanAndError result = sim.run(false, 0.8);
            results.add(result);
            System.out.println(result);
        }
        String filename = String.format("results_Nc_%s.json", Nc);
        writeJSON(results, filename);
    }

    public static void randomSeedTester() {
        for (int i = 0; i < randomNums.size(); i++) {
            double num = randomNums.get(i);
            System.out.format("%20.15f \n", num);
        }
        System.out.println("done with used nums!");
        if (randomNums.size() < 1000) {
            double numMore = 1000 - randomNums.size();
            for (int i = 0; i < numMore; i++) {
                double extraNum = Main.random.nextDouble();
                System.out.format("%20.15f \n", extraNum);
            }
            System.out.println("fully done!");
        }
    }

    public static void runForManyRatios(double rStart, double rEnd, double rIncrement, double dStart, double dEnd, double dIncrement, int count) throws IOException {
        for (double ratio = rStart; ratio <= rEnd; ratio += rIncrement) {
            runForRatioPP(ratio, dStart, dEnd, dIncrement, count);
        }
    }

    // runs simulations for varying values of anode_pos, voltage, and Nc for a single ratio
    public static void runForRatioPP(double ratio, double startNc, double endNc, double increment, int count) throws IOException {
        ArrayList<MeanAndError> theResults = new ArrayList<MeanAndError>();
        for (double Nc = startNc; Nc <= endNc; Nc += increment) {
            SettingsPP.getInstance().setD(0.05);
            SettingsPP.getInstance().setUI(15); // Ui is 15 eV
            SettingsPP.getInstance().setNc(Nc);
            SettingsPP.getInstance().setNi(Nc * (1 / ratio));
            SettingsPP.getInstance().setCount(count);

            // TEMPORARY SOLUTION : still need factory function to make it run w/ both PP and SS
            IGeometry geometry = new ParallelPlate(random);
            Simulation sim = new Simulation(geometry, random);

            MeanAndError result = sim.run(false, 0.8);
            System.out.println("{" + result.Nc + ", Around[" + result.mean + ", " + result.error + "]},");
            theResults.add(result);
        }
        String filename = String.format("results_%s.json", ratio);
        writeJSON(theResults, filename);
    }

    // runs simulations for varying values of anode_pos, voltage, and Nc for a single ratio
    public static void runForOneRatioPP(double ratio, double dStart, double dEnd, double increment, int count) throws IOException {
        ArrayList<MeanAndError> theResults = new ArrayList<MeanAndError>();
        for (double d = dStart; d <= dEnd; d += increment) {
            SettingsPP.getInstance().setD(d);
            SettingsPP.getInstance().setUI(15); // Ui is 15 eV
            SettingsPP.getInstance().setNc(d);
            SettingsPP.getInstance().setCount(count);

            // TEMPORARY SOLUTION : still need factory function to make it run w/ both PP and SS
            IGeometry geometry = new ParallelPlate(random);
            Simulation sim = new Simulation(geometry, random);

            MeanAndError result = sim.run(true, -1.0);
            System.out.println("{" + result.Nc + ", Around[" + result.mean + ", " + result.error + "]},");
            theResults.add(result);
        }
        String filename = String.format("results_%s.json", ratio);
        writeJSON(theResults, filename);
    }

    // write results to json
    private static void writeJSON(ArrayList<MeanAndError> results, String filename) throws IOException {
        // get json gson magic stuff
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // use filewriter to write file
        FileWriter writer = new FileWriter(filename);
        writer.write(gson.toJson(results));
        writer.close();
    }

//    // function that runs sims of a certain NcNi ratio for some range of Nc
//    private static ArrayList<SettingsFresh> makeSettingsForRatio(double startAnodePos, double endAnodePos,
//            double increment, SettingsFresh startSettings) {
//        ArrayList<SettingsFresh> settings1 = new ArrayList<SettingsFresh>();
//        // find E field:
//        double Efield = startSettings.voltage / startSettings.anode_pos;
//        // i is the current Nc
//        for (double i = startAnodePos; i < endAnodePos; i += increment) {
//            // make new settings object for each sim
//            SettingsFresh s1 = new SettingsFresh();
//            s1.lambda_c = startSettings.lambda_c; // in m
//            s1.anode_pos = i; // in m 
////            s1.voltage = Efield * s1.anode_pos; // in volts
//            s1.voltage = s1.anode_pos;
//            s1.lambda_i = startSettings.lambda_i; // in m
//            s1.U_i = startSettings.U_i; // in electronvolts
//            s1.reps = startSettings.reps; // unitless
//            s1.tolerance = startSettings.tolerance; // in m
//
//            // lambda_i = 1 --> distance and voltage are the same
//            settings1.add(s1);
//        }
//        return settings1;
//    }
    // parsing commandline arguments (googled how to do)
    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();
        Option input = new Option("i", "input", true, "Input file name");
        options.addOption(input);
//        Option ratio = new Option("r", "ratio", true, "Ni / Nc ratio");
//        Option reps = new Option("n", "reps", true, "reps for running simulation");
//        Option startNc = new Option("st", "startNc", true, "start value of Nc");
//        Option endNc = new Option("en", "endNc", true, "end value of Nc");
//        Option increment = new Option("in", "increment", true, "Nc increment");
//        options.addOption(ratio);
//        options.addOption(reps);
//        options.addOption(startNc);
//        options.addOption(endNc);
//        options.addOption(increment);
        // Create an Option for each of parameters to makeSettingsForRatio
        // Add them to optons
        // Add option for output file (results) and add to Loop config (maybe to all configs)
        // In main() when there is no input in options you get your settings from
        // makeSettingsForRatio instead of reading it from file

        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            return cmd;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ElectronMonteCarloSimulation", options);

            System.exit(1);
        }
        return null;
    }
}
