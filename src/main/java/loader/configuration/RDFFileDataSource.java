package loader.configuration;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

/**
 * Created by noor on 01/10/17.
 */
public class RDFFileDataSource extends DataSource {
    String liftingRuleLocation;
    public RDFFileDataSource(String dataSourceIRI){
        super(dataSourceIRI);
    }
    public RDFFileDataSource(String dataSourceIRI,String location) {
        super(dataSourceIRI,location);
        loadModel();
    }

    public void setLocation(String location) {
        this.location = location;
        loadModel();
    }

    public void loadModel(){
        if (super.getIRI().equals("DefaulDatasource")){
            model = Global.defaultmodel;
        } else if (model==null){
            System.out.println("Load non default model");
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

    public String getLiftingRuleLocation() {
        return liftingRuleLocation;
    }

    public void setLiftingRuleLocation(String liftingRuleLocation) {
        this.liftingRuleLocation = liftingRuleLocation;
    }

    @Override
    public Model getSelfModel() {

        Model self = super.getSelfModel();
        Resource ds = ResourceFactory.createResource(getIRI());

        if (liftingRuleLocation!=null){
            Property liftingRule = ResourceFactory.createProperty(Global.vocabularyPrefix + "liftingRule");
            Statement st3 = ResourceFactory.createStatement(ds, liftingRule, ResourceFactory.createPlainLiteral(getLiftingRuleLocation()));
            self.add(st3);
        }

        return self;
    }


}
