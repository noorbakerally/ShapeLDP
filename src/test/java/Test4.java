/**
 * Created by bakerally on 11/21/17.
 */

import genPLDPD.Evaluation;
import loader.configuration.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.util.IsoMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class Test4 {

    @Test
    public void loadingFromSPARQLEndpoint() {
        String ldpDatasetPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"LDPDataset.trig");

        //global params
        Global.physical = true;
        Evaluation.base = "";

        //load the design document
        File designDocument = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocument.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), null);

        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        Dataset actualDataset = DatasetFactory.create();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        RDFDataMgr.read(actualDataset,new StringReader(data),"http://example.com",Lang.TRIG);


        writer = new StringWriter();
        RDFDataMgr.write(writer, actualDataset.asDatasetGraph(), Lang.TRIG) ;
        data = writer.toString();
        //System.out.println("=================Actual Dataset=================");
        //System.out.println(data);


        //load the expected LDP dataset with an absolute IRI
        Dataset expectedLDPDataset = DatasetFactory.create();
        RDFDataMgr.read(expectedLDPDataset,ldpDatasetPath,"http://example.com",Lang.TRIG);
        writer = new StringWriter();
        RDFDataMgr.write(writer, expectedLDPDataset.asDatasetGraph(), Lang.TRIG) ;
        String data1 = writer.toString();
        //System.out.println("=================Expected Dataset=================");
        //System.out.println(data1);


        //Compare actual dataset generated from the result with the expected dataset
        Assert.assertTrue(IsoMatcher.isomorphic(actualDataset.asDatasetGraph(),expectedLDPDataset.asDatasetGraph()));
    }
}
