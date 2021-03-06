package com.github.shapeldp.run;

import com.github.shapeldp.ddcomponents.*;
import com.github.shapeldp.evaluation.Evaluation;
import com.github.shapeldp.evaluation.Global;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by bakerally on 11/7/17.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );
    public static void main(String [] args)  {
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

        LOGGER.info("Checking for main data source");

        DataSource dataSource = null;
        if (cl.hasOption("if")){
            inputDataSource = cl.getOptionValue("if");
            dataSource = new RDFFileDataSource("DefaulDataSource");
            dataSource.setLocation(inputDataSource);
            LOGGER.info("Using default data source for all ResourceMap:"+inputDataSource);
            if (cl.hasOption("lf")){
                String liftingRule = cl.getOptionValue("lf");
                ((RDFFileDataSource)dataSource).setLiftingRuleLocation(liftingRule);
            }
        } else if (cl.hasOption("se")){
            inputDataSource = cl.getOptionValue("se");
            dataSource  = new SPARQLDataSource("DefaulDataSource",inputDataSource);
        }

        
        if (cl.hasOption("d")){
            String designDocumentPath = cl.getOptionValue("d");
            try{
                LOGGER.info("Loading the design documnent from:"+designDocumentPath);
                File file = new File(designDocumentPath);
                dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath(),dataSource);
            } catch (Exception exception){
                LOGGER.info("Error while trying to load file:"+designDocumentPath);
                exception.printStackTrace();
            }
        }

        if (cl.hasOption("v")){
            Global.physical = false;
        }


        LOGGER.info("Evaluating design document:Started");
        long startTime = System.currentTimeMillis();
        Dataset ds = Evaluation.evalDD(dd,base);
        long endTime = System.currentTimeMillis();
        LOGGER.info("Evaluating design document:Completed");
        long timeTaken = endTime - startTime;


        System.out.println("Time taken:"+timeTaken);
        System.out.println("ResourceMaps executed:"+ Global.resourceMapsExecuted);
        System.out.println("#start");
        System.out.println(timeTaken+","+Global.resourceMapsExecuted);
        System.out.println("#end");

        if (cl.hasOption("o")){
            outputFile = cl.getOptionValue("o");
            if (outputFile.equals("0")){
                LOGGER.info("No output for LDP Dataset");
            }
            else {
                LOGGER.info("Writing LDPDataset to "+outputFile);
                StringWriter writer = new StringWriter();
                RDFDataMgr.write(writer, ds, Lang.TRIG) ;
                String data = writer.toString();
                PrintStream out = null;
                File f = new File(outputFile);
                if(!f.exists()){
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out = new PrintStream(new FileOutputStream(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                out.print(data);
                out.flush();
            }

        } else {
            RDFDataMgr.write(System.out, ds, Lang.TRIG) ;
        }

        if (cl.hasOption("v")){
            String virtualFile = cl.getOptionValue("v");
            LOGGER.info("virtual file"+virtualFile);
            if (!virtualFile.equals("0")){
                LOGGER.info("Writing virtual graph to "+virtualFile);
                StringWriter writer = new StringWriter();
                RDFDataMgr.write(writer, Global.virtualModel, Lang.TURTLE) ;
                String data = writer.toString();
                PrintStream out = null;
                File f = new File(virtualFile);
                if(!f.exists()){
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out = new PrintStream(new FileOutputStream(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                out.print(data);
                out.flush();
            }
        }

    }
}
