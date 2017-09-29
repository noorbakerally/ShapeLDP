package loader.configuration;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Created by noor on 29/09/17.
 */
public class DesignDocumentFactory {
    static DesignDocument designDocument;
    public static Model model;

    public  static DesignDocument createConfigurationFromDD(String ddLocation){
        designDocument = new DesignDocument();
        model = RDFDataMgr.loadModel(ddLocation);


        return designDocument;
    }
}
