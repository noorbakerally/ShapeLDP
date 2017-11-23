package loader.configuration;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

/**
 * Created by noor on 18/11/17.
 */
public class SPARQLDataSource extends DataSource {

    public SPARQLDataSource(String dataSourceIRI) {
        super(dataSourceIRI);
    }

    public SPARQLDataSource(String dataSourceIRI, String location) {
        super(dataSourceIRI, location);
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void load() {

    }

    @Override
    public ResultSet executeResourceQuery(String queryStr) {
        queryStr = Global.prefixes + queryStr;
        QueryExecution qe = QueryExecutionFactory.sparqlService(this.location, queryStr);
        return qe.execSelect();
    }

    @Override
    public Model executeGraphQuery(String queryStr) {
        queryStr = Global.prefixes + queryStr;
        QueryExecution qe = QueryExecutionFactory.sparqlService(this.location, queryStr);
        return qe.execConstruct();
    }

    @Override
    public Model getSelfModel() {
        Model self = ModelFactory.createDefaultModel();
        Resource ds = ResourceFactory.createResource(getIRI());

        Statement st1 = ResourceFactory.createStatement(ds, RDF.type, ResourceFactory.createResource(Global.vocabularyPrefix + "SPARQLDataSource"));
        self.add(st1);

        Property locationPre = ResourceFactory.createProperty(Global.vocabularyPrefix + "location");
        Statement st2 = ResourceFactory.createStatement(ds, locationPre, ResourceFactory.createPlainLiteral(getLocation()));
        self.add(st2);

        return self;
    }
}
