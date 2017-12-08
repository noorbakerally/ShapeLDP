package com.github.shapeldp.ddcomponents;

import com.github.shapeldp.evaluation.Global;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

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

    public abstract void load();

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public abstract ResultSet executeResourceQuery(String query);


    public abstract Model executeGraphQuery(String query);

    public Model getSelfModel(){
        Model self = ModelFactory.createDefaultModel();
        Resource ds = ResourceFactory.createResource(getIRI());

        Statement st1 = ResourceFactory.createStatement(ds, RDF.type, ResourceFactory.createResource(Global.vocabularyPrefix + "DataSource"));
        self.add(st1);

        Property locationPre = ResourceFactory.createProperty(Global.vocabularyPrefix + "location");
        Statement st2 = ResourceFactory.createStatement(ds, locationPre, ResourceFactory.createPlainLiteral(getLocation()));
        self.add(st2);

        return self;
    }
}
