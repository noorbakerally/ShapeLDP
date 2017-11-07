package run;

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
                    .addOption("d", "designdocument", true, "Path to design document")
                    .addOption("b", "baseURL", true, "URL of the LDP endpoint where POST request have to be sent")
                    .addOption("s","source document",true,"source document describing the data sources")
                    .addOption("is","main input source",true,"main input source for all resource map")
                    .addOption("l", false, "Disable logging, by default logging is enabled")
                    .addOption("v", false, "Generate Virtual LDP design document")
                    ;
            return opt;
        }
        public static void displayHelp() {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("LDPizer", getCMDOptions());
            System.exit(1);
        }
}
