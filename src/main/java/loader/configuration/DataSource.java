package loader.configuration;

import org.apache.jena.rdf.model.Model;

/**
 * Created by noor on 29/09/17.
 */
public class DataSource {
    String IRI;
    String location;
    Model model;

    public DataSource(String dataSourceIRI) {
        this.IRI = dataSourceIRI;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
