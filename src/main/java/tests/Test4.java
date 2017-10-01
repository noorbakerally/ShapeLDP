package tests;

import genPLDPD.Evaluation;
import ldp.Container;
import loader.configuration.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.util.Map;

/**
 * Created by noor on 01/10/17.
 */
public class Test4 {
    public Test4() {
        //test resource map

        String base = "http://www.example.com";
        Evaluation.base = base;
        Global.prefixes = "PREFIX dcat: <http://www.w3.org/ns/dcat#>";
        ClassLoader classLoader = Test4.class.getClassLoader();
        File file = new File(classLoader.getResource("designdocument.ttl").getFile());
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();

        Dataset ds = Evaluation.evalDD(dd,base);

        RDFDataMgr.write(System.out, ds, Lang.TRIG) ;


    }
}
