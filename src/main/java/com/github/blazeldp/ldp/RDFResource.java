package com.github.blazeldp.ldp;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Created by noor on 01/10/17.
 */
public class RDFResource {
    String IRI;
    Model model;
    public RDFResource(String IRI) {
        this.IRI = IRI;
        model = ModelFactory.createDefaultModel();
    }

    public String getIRI() {
        return IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void mergeModel(Model newModel){
        model = model.union(newModel);
    }

}
