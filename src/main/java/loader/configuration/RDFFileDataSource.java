package loader.configuration;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Created by noor on 01/10/17.
 */
public class RDFFileDataSource extends DataSource{
    public RDFFileDataSource(String dataSourceIRI){
        super(dataSourceIRI);
    }
    public RDFFileDataSource(String dataSourceIRI,String location) {
        super(dataSourceIRI,location);
        loadModel();
    }

    public void loadModel(){
        if (model==null){
            model = ModelFactory.createDefaultModel();
            model.read(location);
        }
    }

    public ResultSet executeResourceQuery(String query) {
        return Global.exeQuery(query,model);
    }

    public Model executeGraphQuery(String query) {
        return Global.exeGraphQuery(query,model);
    }


}
