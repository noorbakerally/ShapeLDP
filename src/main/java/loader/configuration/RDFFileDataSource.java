package loader.configuration;

/*import com.github.thesmartenergy.sparql.generate.jena.SPARQLGenerate;
import com.github.thesmartenergy.sparql.generate.jena.engine.PlanFactory;
import com.github.thesmartenergy.sparql.generate.jena.engine.RootPlan;
import com.github.thesmartenergy.sparql.generate.jena.query.SPARQLGenerateQuery;*/
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void load(){
        if (super.getIRI().equals("DefaulDatasource")){
            model = Global.defaultmodel;
        } else if (model==null){
            System.out.println("Load non default model");
            if (liftingRuleLocation == null){
                model = ModelFactory.createDefaultModel();
                model.read(location);
            }  else {

                try {

                    //loading the lifting rule
                    InputStream inputStream = new URL(liftingRuleLocation).openStream();
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
                    HttpResponse response = client.execute(post);
                    String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

                    //deconding ther response
                    JsonObject obj = JSON.parse(result);
                    String output = obj.get("output").getAsString().value();

                    //loading the model
                    this.model = ModelFactory.createDefaultModel();
                    this.model.read(new ByteArrayInputStream(output.getBytes()) ,null,Lang.TURTLE.getLabel());



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                /*
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
