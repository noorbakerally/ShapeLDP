package loader.configuration;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

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
}
