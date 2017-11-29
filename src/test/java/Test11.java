import genPLDPD.Evaluation;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.Global;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.util.IsoMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by bakerally on 11/21/17.
 */
public class Test11 {

    @Test
    public void virtualGraph() {
        String ldpDatasetPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"LDPDataset.trig");
        String virtualGraphPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"VirtualGraph.ttl");


        //global params
        Global.physical = false;
        Evaluation.base = "";

        //load the design document
        File designDocument = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocument.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), null);

        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        PrintWriter out;
        try {
            out = new PrintWriter("/home/noor/Downloads/parking/graph.trig");
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
            TESTING OF VIRTUAL GRAPH
        */
        //testing the virtual grph
        Model resultVirtualGraph = Global.virtualModel;
        writer = new StringWriter();
        RDFDataMgr.write(writer, resultVirtualGraph, Lang.TURTLE) ;
        data = writer.toString();
        try {
            out = new PrintWriter("/home/noor/Downloads/parking/vgraph.trig");
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}
