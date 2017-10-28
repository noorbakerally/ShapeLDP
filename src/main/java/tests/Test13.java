package tests;

import genPLDPD.Evaluation;
import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.NonContainerMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.util.Map;

/**
 * Created by noor on 26/10/17.
 */
public class Test13 {
    public Test13() {
        String base = "http://www.example.com";
        Evaluation.base = base;
        ClassLoader classLoader = Test4.class.getClassLoader();
        File file = new File(classLoader.getResource("Test13.ttl").getFile());
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        Dataset ds = Evaluation.evalDD(dd,base);
        RDFDataMgr.write(System.out, ds, Lang.TRIG) ;

    }
}
