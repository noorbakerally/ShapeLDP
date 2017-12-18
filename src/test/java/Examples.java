/**
 * Created by bakerally on 11/21/17.
 */

import com.github.shapeldp.ddcomponents.DataSource;
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

public class Examples {

    @Test
    public void loadingFromRDFFile() {


        String ex1input = TestUtilities.
                getTestResourceAbsPath(getClass().getSimpleName()+"/Example1","RDFGraph.ttl");
        //global params
        Global.physical = true;
        Evaluation.base = "";

        //set the data source
        DataSource mainDataSource = new RDFFileDataSource("DefaultDataSource");
        mainDataSource.setLocation(ex1input);

        //load the design document
        File designDocument = TestUtilities.
                getTestResource(getClass().getSimpleName()+"/Example1","DesignDocument.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), mainDataSource);

        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        System.out.println(data);



    }

}
