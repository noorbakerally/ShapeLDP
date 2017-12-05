package com.github.blazeldp.run;

import org.apache.commons.cli.*;

/**
 * Created by bakerally on 11/7/17.
 */
public class CMDConfigurations {
        public static CommandLine parseArguments(String[] args) throws ParseException {

            BasicParser commandLineParser = new BasicParser();
            CommandLine cl = commandLineParser.parse(getCMDOptions(), args);

            /*Process Options*/
            //print help menu
            if ( cl.hasOption('h') ) {
                CMDConfigurations.displayHelp();
            }
            return cl;
        }
        public static Options getCMDOptions(){
            Options opt = new Options()
                    .addOption("h", "help",false, "Show help")
                    .addOption("o", "outputfile", true, "Path to LDP dataset")
                    .addOption("d", "designDocument", true, "Path to design document")
                    .addOption("s","sourcesDocument",true,"source document describing the data sources")
                    .addOption("if","inputFile",true,"URL for a file as the main input source")
                    .addOption("lf","liftingRule",true,"lifting rule for main input source")
                    .addOption("se","sparqlEndpoint",true,"URL for a SPARQL endpoint to act as main input source")
                    .addOption("l", false, "Disable logging, by default logging is enabled")
                    .addOption("v", "virtualGraph",true, "Path to virtual graph")
                    ;
            return opt;
        }
        public static void displayHelp() {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("LDPizer", getCMDOptions());
            System.exit(1);
        }
}
