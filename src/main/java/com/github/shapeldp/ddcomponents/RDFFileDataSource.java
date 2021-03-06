package com.github.shapeldp.ddcomponents;

/*import com.github.thesmartenergy.sparql.generate.jena.SPARQLGenerate;
import com.github.thesmartenergy.sparql.generate.jena.engine.PlanFactory;
import com.github.thesmartenergy.sparql.generate.jena.engine.RootPlan;
import com.github.thesmartenergy.sparql.generate.jena.query.SPARQLGenerateQuery;*/
import com.github.shapeldp.evaluation.Global;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by noor on 01/10/17.
 */
public class RDFFileDataSource extends DataSource {
    private static final Logger LOGGER = Logger.getLogger(RDFFileDataSource.class.getSimpleName());

    String liftingRuleLocation;
    public RDFFileDataSource(String dataSourceIRI){
        super(dataSourceIRI);
    }

    public RDFFileDataSource(String dataSourceIRI,String location) {
        super(dataSourceIRI,location);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void load(){

        if (model == null){
            if (liftingRuleLocation==null){
                model = ModelFactory.createDefaultModel();
                model.read(location,"TURTLE");
            } else {
                //loading the model via the lifting ruke
                try {
                    //loading the lifting rule
                    InputStream inputStream = new URL(liftingRuleLocation).openStream();
                    LOGGER.info("Fetching lifting rule from:"+liftingRuleLocation);
                    String queryString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                    //creating the post request
                    HttpClient client = HttpClientBuilder.create().build();
                    String url = "https://ci.mines-stetienne.fr/sparql-generate/api/transform";
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("query",
                            queryString));
                    post.setHeader("Content-type","application/x-www-form-urlencoded");
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //sending the request to SPARQL Generate API
                    LOGGER.info("Sending request for transformation to SPARQL Generate API");
                    HttpResponse response = client.execute(post);
                    String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

                    //deconding ther response
                    JsonObject obj = JSON.parse(result);
                    String output = obj.get("output").getAsString().value();

                    //loading the model
                    this.model = ModelFactory.createDefaultModel();
                    this.model.read(new ByteArrayInputStream(output.getBytes()) ,null,Lang.TURTLE.getLabel());
                    LOGGER.info("Model loaded");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* IF SPARQL Generate JAR is used
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
        }*/
    }

    public ResultSet executeResourceQuery(String query) {
        if (model == null) load();
        return Global.exeQuery(query,model);
    }

    public Model executeGraphQuery(String query) {
        if (model == null) load();
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
