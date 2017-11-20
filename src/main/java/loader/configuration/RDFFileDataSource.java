package loader.configuration;

import com.github.thesmartenergy.sparql.generate.jena.SPARQLGenerate;
import com.github.thesmartenergy.sparql.generate.jena.engine.PlanFactory;
import com.github.thesmartenergy.sparql.generate.jena.engine.RootPlan;
import com.github.thesmartenergy.sparql.generate.jena.query.SPARQLGenerateQuery;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
            if (liftingRuleLocation == null){
                model = ModelFactory.createDefaultModel();
                model.read(location);
            } else {
                InputStream inputStream = null;
                try {
                    inputStream = new URL(liftingRuleLocation).openStream();
                    String queryString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    SPARQLGenerateQuery query = (SPARQLGenerateQuery) QueryFactory.create(queryString, SPARQLGenerate.SYNTAX);
                    PlanFactory planFactory = new PlanFactory();
                    RootPlan plan = planFactory.create(query);
                    model = plan.exec();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
