import genPLDPD.Evaluation;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.Global;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by bakerally on 11/23/17.
 */
public class Test10 {
    @Test
    public void generateLanguagesLDPDataset() {
        //global params
        Global.physical = true;
        Evaluation.base = "";

        //load the design document
        File designDocument = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocument.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), null);

        System.out.println(Global.prefixMap);
        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        System.out.println("=================Actual Dataset=================");
        //System.out.println(data);

        try {
            PrintWriter out = new PrintWriter("/home/noor/Downloads/result.txt");
            out.println( data );
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
