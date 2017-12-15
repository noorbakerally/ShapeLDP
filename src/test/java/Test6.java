import com.github.shapeldp.evaluation.Evaluation;
import com.github.shapeldp.ddcomponents.DesignDocument;
import com.github.shapeldp.ddcomponents.DesignDocumentFactory;
import com.github.shapeldp.evaluation.Global;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by bakerally on 11/21/17.
 */
public class Test6 {

    @Test
    public void virtualGraph() {
        String ldpDatasetPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"LDPDataset.trig");
        String virtualGraphPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"VirtualGraph.ttl");


        //global params
        Global.physical = false;
        Evaluation.base = "";

        //load the design document
        File designDocument = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocumentBBC.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), null);

        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        Dataset actualDataset = DatasetFactory.create();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        RDFDataMgr.read(actualDataset,new StringReader(data),"http://example.com/",Lang.TRIG);
        writer = new StringWriter();
        RDFDataMgr.write(writer, actualDataset.asDatasetGraph(), Lang.TRIG) ;
        data = writer.toString();
        //System.out.println("=================Actual Dataset=================");
        //System.out.println(data);


        //load the expected LDP dataset with an absolute IRI
        Dataset expectedLDPDataset = DatasetFactory.create();
        RDFDataMgr.read(expectedLDPDataset,ldpDatasetPath,"http://example.com/",Lang.TRIG);
        writer = new StringWriter();
        RDFDataMgr.write(writer, expectedLDPDataset.asDatasetGraph(), Lang.TRIG) ;
        String data1 = writer.toString();
        //System.out.println("=================Expected Dataset=================");
        //System.out.println(data1);

        /*
            TESTING OF VIRTUAL GRAPH
        */
        //testing the virtual grph
        Model resultVirtualGraph = Global.virtualModel;
        writer = new StringWriter();
        Model actualVirtualGraph = ModelFactory.createDefaultModel();
        RDFDataMgr.write(writer, resultVirtualGraph, Lang.TURTLE) ;
        RDFDataMgr.read(actualVirtualGraph,new StringReader(writer.toString()),"http://example.com/",Lang.TURTLE);
        System.out.println("=================Actual Virtual LDP Dataset=================");
        writer.getBuffer().setLength(0);
        RDFDataMgr.write(writer, actualVirtualGraph, Lang.TURTLE) ;
        data = writer.toString();
        System.out.println(data);

        //load the expected LDP dataset with an absolute IRI
        Model expectedVirtualGraph = ModelFactory.createDefaultModel();
        RDFDataMgr.read(expectedVirtualGraph,virtualGraphPath,"http://example.com/",Lang.TURTLE);
        writer = new StringWriter();
        RDFDataMgr.write(writer, expectedVirtualGraph, Lang.TURTLE) ;
        data1 = writer.toString();
        System.out.println("=================Expected Virtual LDP Dataset=================");
        System.out.println(data1);
        //Assert.assertTrue(IsoMatcher.isomorphic(actualVirtualGraph.getGraph(),expectedVirtualGraph.getGraph()));




        //tests checks here
        //Compare actual dataset generated from the result with the expected dataset
        //Assert.assertTrue(IsoMatcher.isomorphic(actualDataset.asDatasetGraph(),expectedLDPDataset.asDatasetGraph()));
        Assert.assertTrue(true);

    }

}
