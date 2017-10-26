package tests;

import genPLDPD.Evaluation;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;

/**
 * Created by noor on 26/10/17.
 */
public class Test8 {
    public Test8() {
        //test resource map
        String base = "http://www.example.com";
        Evaluation.base = base;
        ClassLoader classLoader = Test8.class.getClassLoader();
        File file = new File(classLoader.getResource("ddNull2.ttl").getFile());
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Dataset ds = Evaluation.evalDD(dd,base);
        RDFDataMgr.write(System.out, ds, Lang.TRIG);
    }
}
