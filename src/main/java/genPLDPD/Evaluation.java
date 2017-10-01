package genPLDPD;

import ldp.Container;
import ldp.RDFResource;
import loader.configuration.*;
import org.apache.jena.atlas.lib.ListUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;

import java.util.*;

/**
 * Created by noor on 30/09/17.
 */
public class Evaluation {
    public static String base;

    public static Dataset evalDD(DesignDocument dd,String base){
        Dataset ldpDD = DatasetFactory.create();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        for (Map.Entry <String,NonContainerMap> nonContainerMapEntry:nonContainerMaps.entrySet()){
            String iri = nonContainerMapEntry.getKey();
            NonContainerMap nonContainerMap = nonContainerMapEntry.getValue();
            EvalResult evalResult = evalNM(nonContainerMap, new Container(base), null, ldpDD);
            ldpDD = Utilities.mergeDataSet(ldpDD,evalResult.getDs());
        }
        return ldpDD;
    }

    public static EvalResult evalNM(NonContainerMap nonContainerMap, Container container,RDFResource relatedResource, Dataset ds){
        Dataset dt = DatasetFactory.create();
        List <GenID> genIDS = new ArrayList<GenID>();
        for (Map.Entry <String,ResourceMap> resourceMapEntry:nonContainerMap.getResourceMaps().entrySet()){
            ResourceMap currentResourceMap = resourceMapEntry.getValue();
            Dataset tempDS = Utilities.mergeDataSet(dt, ds);
            EvalResult evalResult = evalRM(currentResourceMap, nonContainerMap, container, relatedResource, tempDS);
            dt = Utilities.mergeDataSet(dt,evalResult.getDs());
            genIDS.addAll(evalResult.getGenIDs());
        }
        EvalResult evalResult = new EvalResult(genIDS,dt);
        return evalResult;
    }

    public static EvalResult evalRM(ResourceMap resourceMap,HasResourceMap parentMap,Container container, RDFResource relatedResource, Dataset ds){
        Dataset dt = DatasetFactory.create();
        List<GenID> genIDts = new ArrayList<GenID>();

        String resourceQuery = resourceMap.getResourceQuery();
        String finalQuery = "Select * WHERE "+resourceQuery;

        //replace the parent resource if it exist in the query
        if (relatedResource != null){
            resourceQuery.replace("?pres","<"+relatedResource.getIRI()+">");
        }

        //a list to hold all resources generated from all datasources
        //and their model
        Map <String,RDFResource> resources = new HashMap<String, RDFResource>();


        //for each data source, execute the resource query, get all the resources,
        //create a map for the resources with their model
        //get the graph of the resources from the data source from which they were generated
        for (Map.Entry<String,DataSource> datasourceEntry:resourceMap.getDataSources().entrySet()){
            DataSource dataSource = datasourceEntry.getValue();
            ResultSet rs = dataSource.executeResourceQuery(finalQuery);
            while (rs.hasNext()){
                QuerySolution qs = rs.next();

                //get the current resource IRI
                String resourceIRI = qs.get("?res").toString();

                //create the resource in the list
                if (!resources.containsKey(resourceIRI)){
                    resources.put(resourceIRI,new RDFResource(resourceIRI));
                }

                //get the resource graph
                String graphQuery = resourceMap.getGraphQuery();
                graphQuery = graphQuery.replace("?res","<"+resourceIRI+">");
                Model tempModel = dataSource.executeGraphQuery(graphQuery);
                resources.get(resourceIRI).mergeModel(tempModel);

            }

        }

        //iterate through all resources generated from datasources
        for (Map.Entry<String,RDFResource> rdfResourceEntry:resources.entrySet()){
            String currentResourceIRI = rdfResourceEntry.getKey();
            RDFResource currentResource = rdfResourceEntry.getValue();
            String newIRI = Utilities.genIRI(base,currentResourceIRI);

            //generate the entire model here for the LDP resource
            //currently using only the direct model generate from the datasources
            Dataset dtNew = DatasetFactory.create();

            //add type iri here
            Model newModel = ModelFactory.createDefaultModel();
            String newType = Global.getLDPRTypeIRI(parentMap.getType());
            newModel = newModel.add(ResourceFactory.createResource(newIRI),
                    RDF.type,ResourceFactory.createResource(newType));

            //temporary, to replace with graph patterns from parentMap
            newModel = newModel.add(ResourceFactory.createResource(newIRI),
                    FOAF.primaryTopic,ResourceFactory.createResource(currentResourceIRI));

            dtNew.addNamedModel(newIRI,currentResource.getModel().union(newModel));



            dt = Utilities.mergeDataSet(dt,dtNew);

            Dataset dtContainer = DatasetFactory.create();
            Model modelCont = ModelFactory.createDefaultModel();

            Resource containerResource = ResourceFactory.createResource(container.getIRI());
            Property contains = ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains");
            Resource newLDPResource = ResourceFactory.createResource(newIRI);
            modelCont = modelCont.add(containerResource,contains,newLDPResource);
            dtContainer.addNamedModel(container.getIRI(),modelCont);
            dt = Utilities.mergeDataSet(dt,dtContainer);
            genIDts.add(new GenID(newIRI,currentResourceIRI));
        }
        return new EvalResult(genIDts,dt);
    }

    public static class GenID{
        String ldprIRI;
        String rdfResourceIRI;

        public GenID(String ldprIRI, String rdfResourceIRI) {
            this.ldprIRI = ldprIRI;
            this.rdfResourceIRI = rdfResourceIRI;
        }
    }

    public static class EvalResult{
        List<GenID> genIDs;
        Dataset ds;

        public EvalResult() {
            this.genIDs = new ArrayList<GenID>();
            this.ds = DatasetFactory.create();
        }

        public EvalResult(List<GenID> genIDs,Dataset ds) {
            this.genIDs = genIDs;
            this.ds = ds;
        }

        public void addGenID(Container container, RDFResource rdfResource){
            GenID genID = new GenID(container.getIRI(), rdfResource.getIRI());
            genIDs.add(genID);
        }

        public List<GenID> getGenIDs() {
            return genIDs;
        }

        public void setGenIDs(List<GenID> genIDs) {
            this.genIDs = genIDs;
        }

        public Dataset getDs() {
            return ds;
        }

        public void setDs(Dataset ds) {
            this.ds = ds;
        }
    }



}
