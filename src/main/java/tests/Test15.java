package tests;

import genPLDPD.Evaluation;
import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.NonContainerMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.*;
import java.util.Map;

/**
 * Created by noor on 26/10/17.
 */
public class Test15 {
    public Test15() {
        String base = "";
        Evaluation.base = base;
        ClassLoader classLoader = Test4.class.getClassLoader();
        File file = new File(classLoader.getResource("Test15.ttl").getFile());
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        System.out.println("Evaluating design document:Started");
        Dataset ds = Evaluation.evalDD(dd,base);
        System.out.println("Evaluating design document:Completed");
        //RDFDataMgr.write(System.out, ds, Lang.TRIG) ;
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, ds, Lang.TRIG) ;
        String data = writer.toString();
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("/home/bakerally/Downloads/test.ttl"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.print(data);
    }
}
