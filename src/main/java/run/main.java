package run;

import genPLDPD.Evaluation;
import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.NonContainerMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import tests.Test15;
import tests.Test4;

import java.io.*;
import java.util.Map;

/**
 * Created by bakerally on 11/7/17.
 */
public class main {
    public static void main(String [] args){
        CommandLine cl = null;
        try {
            cl = CMDConfigurations.parseArguments(args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cl.getOptions().length == 0){
            CMDConfigurations.displayHelp();
            return;
        }


        String base = "";
        Evaluation.base = base;
        DesignDocument dd = null;
        String outputFile = null;
        
        if (cl.hasOption("d")){
            String designDocumentPath = cl.getOptionValue("d");
            try{

                System.out.println("Loading the design documnent from:"+designDocumentPath);
                File file = new File(designDocumentPath);
                dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
            } catch (Exception exception){
                System.out.println("Error while trying to load file:"+designDocumentPath);
            }
        }

        
        System.out.println("Evaluating design document:Started");
        Dataset ds = Evaluation.evalDD(dd,base);
        System.out.println("Evaluating design document:Completed");


        if (cl.hasOption("o")){
            outputFile = cl.getOptionValue("d");
            StringWriter writer = new StringWriter();
            RDFDataMgr.write(writer, ds, Lang.TRIG) ;
            String data = writer.toString();
            PrintStream out = null;
            try {
                out = new PrintStream(new FileOutputStream(outputFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            out.print(data);
        } else {
            RDFDataMgr.write(System.out, ds, Lang.TRIG) ;
        }





    }
}
