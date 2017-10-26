package loader.configuration;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

/**
 * Created by noor on 29/09/17.
 */
public abstract class DataSource {
    String IRI;
    String location;
    Model model;

    public DataSource(String dataSourceIRI){
        this.IRI = dataSourceIRI;
    }
    public DataSource(String dataSourceIRI,String location) {
        this.IRI = dataSourceIRI;
        this.location = location;
    }

    public String getIRI() {
        return IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public String getLocation() {
        return location;
    }

    public abstract void setLocation(String location);


    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public abstract ResultSet executeResourceQuery(String query);


    public abstract Model executeGraphQuery(String query);
}
