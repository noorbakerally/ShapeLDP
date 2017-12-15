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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import java.io.*;

public class Test8 {

    @Test
    public void loadingFromRDFFile() {
        //global params
        Global.physical = true;
        Evaluation.base = "";

        //set the data source
        File is = TestUtilities.getTestResource(getClass().getSimpleName(),"is2.ttl");

        DataSource mainDataSource = new RDFFileDataSource("DefaultDataSource");
        //mainDataSource.setLocation("https://bistrotdepays.opendatasoft.com/api/v2/catalog/exports/ttl");
        //mainDataSource.setLocation(is.getAbsolutePath());
        mainDataSource.setLocation("/home/noor/Documents/repositories/github/bbc-wildlife/all.nt");

        //load the design document
        File designDocument = TestUtilities.getTestResource(getClass().getSimpleName(),"DesignDocument.ttl");
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(designDocument.getAbsolutePath(), mainDataSource);

        //evaluate the design document
        Dataset resultDataset = Evaluation.evalDD(dd,"");

        //create the actual dataset with an absolute IRI using the resultDataset
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, resultDataset.asDatasetGraph(), Lang.TRIG) ;
        String data = writer.toString();
        System.out.println("=================Actual Dataset=================");
        System.out.println(data);

        PrintWriter pwriter = null;
        try {
            pwriter = new PrintWriter("/home/noor/Downloads/results.ttl", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pwriter.println(data);
        pwriter.close();

    }

}
