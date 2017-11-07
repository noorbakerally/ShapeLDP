package run;

import genPLDPD.Evaluation;
import loader.configuration.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;



import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by bakerally on 11/7/17.
 */
public class Main {
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

        String inputDataSource = null;
        if (cl.hasOption("is")){
            inputDataSource = cl.getOptionValue("is");
            Model model = ModelFactory.createDefaultModel();
            Global.defaultmodel = model.read(inputDataSource);
            System.out.println("Using default data source for all ResourceMap:"+inputDataSource);
        }
        if (cl.hasOption("d")){
            String designDocumentPath = cl.getOptionValue("d");
            try{
                System.out.println("Loading the design documnent from:"+designDocumentPath);
                File file = new File(designDocumentPath);
                dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath(),inputDataSource);
            } catch (Exception exception){
                System.out.println("Error while trying to load file:"+designDocumentPath);
            }
        }

        
        System.out.println("Evaluating design document:Started");
        long startTime = System.currentTimeMillis();
        Dataset ds = Evaluation.evalDD(dd,base);
        long endTime = System.currentTimeMillis();
        System.out.println("Evaluating design document:Completed");


        if (cl.hasOption("o")){
            outputFile = cl.getOptionValue("o");
            StringWriter writer = new StringWriter();
            RDFDataMgr.write(writer, ds, Lang.TRIG) ;
            String data = writer.toString();
            PrintStream out = null;

            File f = new File(outputFile);
            if(!f.exists())
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            try {
                out = new PrintStream(new FileOutputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            out.print(data);
            out.flush();

        } else {
            RDFDataMgr.write(System.out, ds, Lang.TRIG) ;
        }
    }
}
