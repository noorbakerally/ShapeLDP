import com.github.shapeldp.ddcomponents.DesignDocument;
import com.github.shapeldp.ddcomponents.DesignDocumentFactory;
import com.github.shapeldp.ddcomponents.RDFFileDataSource;
import com.github.shapeldp.evaluation.Evaluation;
import com.github.shapeldp.evaluation.Global;
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
import java.util.logging.Logger;

/**
 * Created by bakerally on 11/21/17.
 */
public class Test3 {
    private static final Logger LOGGER = Logger.getLogger( Test3.class.getName() );

    @Test
    public void usingLiftingRule() {
        String ldpDatasetPath = TestUtilities.getTestResourceAbsPath(getClass().getSimpleName(),"LDPDataset.trig");

        //global params
        Global.physical = true;
        Evaluation.base = "";

        //set the data source
        RDFFileDataSource mainDataSource = new RDFFileDataSource("DefaultDataSource");

        File capitalJSON = TestUtilities.getTestResource(getClass().getSimpleName(),"capital.json");
        mainDataSource.setLocation("file://"+capitalJSON.getAbsolutePath());

        File queryRQG = TestUtilities.getTestResource(getClass().getSimpleName(),"query.rqg");
        mainDataSource.setLiftingRuleLocation("file://"+queryRQG.getAbsolutePath());


        //load the design document
        File designDocument3 = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocumentBBC.ttl");
        LOGGER.info("Loading design document from:"+designDocument3.getAbsolutePath());
        DesignDocument dd3 = DesignDocumentFactory.createDDFromFile(designDocument3.getAbsolutePath(), mainDataSource);


        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd3,"");

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
