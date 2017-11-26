package genPLDPD;
import ldp.Container;
import ldp.RDFResource;
import loader.configuration.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import run.Main;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by noor on 30/09/17.
 */
public class Evaluation {
    private static final Logger LOGGER = Logger.getLogger( Evaluation.class.getName() );
    public static String base;
    static Dataset ldpDD = DatasetFactory.create();

    public static Dataset evalDD(DesignDocument dd,String base){
        LOGGER.info("Evaluation of design document");

        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        List <String> parents = new ArrayList<String>();

        LOGGER.info("Evaluation of design document ContainerMaps");
        for (Map.Entry <String,ContainerMap> containerMapEntry:containerMaps.entrySet()){
            ContainerMap containerMap = containerMapEntry.getValue();
            EvalResult evalResult = evalCM(containerMap, new Container(base),parents);
            ldpDD = evalResult.getDs();
        }

        LOGGER.info("Evaluation of NonContainerMaps");
        for (Map.Entry <String,NonContainerMap> nonContainerMapEntry:nonContainerMaps.entrySet()){
            NonContainerMap nonContainerMap = nonContainerMapEntry.getValue();
            EvalResult evalResult = evalNM(nonContainerMap, new Container(base), parents);
            ldpDD = evalResult.getDs();
        }
        return ldpDD;
    }

    public static EvalResult evalCM(ContainerMap containerMap, Container container,List <String> parents){
        LOGGER.info("Evaluation of ContainerMap:"+containerMap.getIRI());

        List <GenID> genIDS = new ArrayList<GenID>();
        LOGGER.info("Evaluation of all ResourceMaps for ContainerMap:"+containerMap.getIRI());
        for (Map.Entry <String,ResourceMap> resourceMapEntry:containerMap.getResourceMaps().entrySet()){
            ResourceMap currentResourceMap = resourceMapEntry.getValue();
            EvalResult evalResult = evalRM(currentResourceMap, containerMap, container,new ArrayList<String>(parents));
            ldpDD = evalResult.getDs();
            genIDS.addAll(evalResult.getGenIDs());
        }

        for (GenID genID:genIDS){
            String newIRI = genID.getLdprIRI();
            String resourceIRI = genID.getRdfResourceIRI();
            List <String> newparents1 = new ArrayList<String>(parents);
            newparents1.add(resourceIRI);

            LOGGER.info("Evaluation of all child ContainerMap for ContainerMap:"+containerMap.getIRI());
            for (Map.Entry <String,ContainerMap> cmEntry:containerMap.getContainerMaps().entrySet()){
                ContainerMap cm = cmEntry.getValue();
                EvalResult evalResult = evalCM(cm, new Container(newIRI), new ArrayList<String>(newparents1));
                ldpDD = evalResult.getDs();
            }

            LOGGER.info("Evaluation of all NonContainerMap for ContainerMap:"+containerMap.getIRI());
            for (Map.Entry <String,NonContainerMap> ncmEntry:containerMap.getNonContainerMaps().entrySet()){
                NonContainerMap ncm = ncmEntry.getValue();
                EvalResult evalResult = evalNM(ncm, new Container(newIRI), new ArrayList<String>(newparents1));
                ldpDD = evalResult.getDs();
            }
        }
        return new EvalResult(genIDS,ldpDD);

    }

    public static EvalResult evalNM(NonContainerMap nonContainerMap, Container container,List <String> parents){
        LOGGER.info("Evaluation of NonContainerMap:"+nonContainerMap.getIRI());
        List <GenID> genIDS = new ArrayList<GenID>();
        for (Map.Entry <String,ResourceMap> resourceMapEntry:nonContainerMap.getResourceMaps().entrySet()){
            ResourceMap currentResourceMap = resourceMapEntry.getValue();
            EvalResult evalResult = evalRM(currentResourceMap, nonContainerMap, container,parents);
            ldpDD = evalResult.getDs();
            genIDS.addAll(evalResult.getGenIDs());
        }
        EvalResult evalResult = new EvalResult(genIDS,ldpDD);
        return evalResult;
    }

    public static EvalResult evalRM(ResourceMap resourceMap,HasResourceMap parentMap,Container container, List <String> parents){
        LOGGER.info("Evaluation of ResourceMap:"+resourceMap.getIRI()+" for Map:"+parentMap.getIRI());

        //increase the number of resource maps executed
        Global.resourceMapsExecuted = Global.resourceMapsExecuted +1;


        List<GenID> genIDts = new ArrayList<GenID>();

        String resourceQuery = resourceMap.getResourceQuery();
        resourceQuery = Utilities.processRawQuery(resourceQuery,parents);
        String finalQuery = "Select DISTINCT ?res WHERE "+resourceQuery;

        //a list to hold all resources generated from all datasources
        //and their model
        Map <String,RDFResource> resources = new HashMap<String, RDFResource>();


        //for each data source, execute the resource query, get all the resources,
        //create a map for the resources with their model
        //get the graph of the resources from the data source from which they were generated
        LOGGER.info("Retrieving related resources from DataSource for ResourceMap:"+resourceMap.getIRI());

        Model mergedModel = ModelFactory.createDefaultModel();
        DataSource dataSource = new RDFFileDataSource("D");
        if (resourceMap.getDataSources().entrySet().size() > 1){
            for (Map.Entry<String,DataSource> datasourceEntry:resourceMap.getDataSources().entrySet()) {
                mergedModel = mergedModel.union(datasourceEntry.getValue().getModel());
            }
            dataSource.setModel(mergedModel);
        } else {
            for (Map.Entry<String,DataSource> datasourceEntry:resourceMap.getDataSources().entrySet()) {
                dataSource = datasourceEntry.getValue();
            }
        }



        ResultSet rs = dataSource.executeResourceQuery(finalQuery);
        while (rs.hasNext()){
            QuerySolution qs = rs.next();

            //get the current resource IRI
            RDFNode resource = qs.get("?res");
            if (resource != null){
                String resourceIRI = resource.toString();
                //create the resource in the list
                if (!resources.containsKey(resourceIRI)){
                    resources.put(resourceIRI,new RDFResource(resourceIRI));
                }

                //get the resource graph
                if (Global.physical) {
                    String graphQuery = resourceMap.getGraphQuery();
                    graphQuery = graphQuery.replace("?res", "<" + resourceIRI + ">");
                    Model tempModel = dataSource.executeGraphQuery(graphQuery);
                    resources.get(resourceIRI).mergeModel(tempModel);
                }

            } else {
                String rname = "null"+Math.random();
                resources.put(rname,new RDFResource(rname));
            }
        }
        LOGGER.info("Related resources retrieved from DataSource for ResourceMap:"+resourceMap.getIRI());


        //iterate through all resources generated from datasources
        for (Map.Entry<String,RDFResource> rdfResourceEntry:resources.entrySet()){
            String currentResourceIRI = rdfResourceEntry.getKey();
            RDFResource currentResource = rdfResourceEntry.getValue();

            String newIRI = null;
            if (parentMap.getIRIQueryTemplate() == null){
                newIRI = Utilities.processIRITemplate(parentMap.getIRITemplate(),currentResource,parents);
            } else {
                String querysq = parentMap.getIRIQueryTemplate();
                querysq = querysq.replace("?res", "<" + currentResourceIRI + ">");
                querysq = "SELECT DISTINCT ?template WHERE " + querysq;
                Query gq = QueryFactory.create(Global.prefixes + querysq);
                //gq.setPrefixMapping(Global.dupPrefixMap);
                //gq.getPrologue().getPrefixMapping().clearNsPrefixMap();

                rs = Global.exeQuery(gq.serialize(), currentResource.getModel());
                while (rs.hasNext()){
                    String varResult = rs.next().get("?template").toString();
                    newIRI = varResult;
                }
            }


            if (container.getIRI().length() != 0){
                newIRI = container.getIRI() + "/"+newIRI;
            }


            //add type iri here
            Model newModel = ModelFactory.createDefaultModel();
            String newType = Global.getLDPRTypeIRI(parentMap.getType());
            newModel = newModel.add(ResourceFactory.createResource(newIRI),
                    RDF.type,ResourceFactory.createResource(newType));

            //add ldp:Container, ldp:RDFSource triples
            if (newType.equals("http://www.w3.org/ns/ldp#BasicContainer")){
                newModel = newModel.add(ResourceFactory.createResource(newIRI),
                        RDF.type,ResourceFactory.createResource("http://www.w3.org/ns/ldp#Container"));
                newModel = newModel.add(ResourceFactory.createResource(newIRI),
                        RDF.type,ResourceFactory.createResource("http://www.w3.org/ns/ldp#RDFSource"));
            }

            //temporary, to replace with graph patterns from parentMap
            if (!currentResourceIRI.substring(0,4).equals("null")){
                newModel = newModel.add(ResourceFactory.createResource(newIRI),
                        FOAF.primaryTopic,ResourceFactory.createResource(currentResourceIRI));
            }

            if (ldpDD.containsNamedModel(newIRI)){
                ldpDD.addNamedModel(newIRI,ModelFactory.createDefaultModel());
            }
            ldpDD.replaceNamedModel(newIRI,currentResource.getModel().union(newModel));


            if (container != null){
                Resource containerResource = ResourceFactory.createResource(container.getIRI());
                Property contains = ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains");
                Resource newLDPResource = ResourceFactory.createResource(newIRI);
                ldpDD.getNamedModel(container.getIRI()).add(containerResource,contains,newLDPResource);

            }

            if (!Global.physical){
                if (!resourceMap.getIRI().contains("nullResourceMap")) {
                    Resource newLDPResource = ResourceFactory.createResource(newIRI);

                    Resource compiledRM = ResourceFactory.createResource();
                    Property propertyRM = ResourceFactory.createProperty(Global.vocabularyPrefix + "compiledResourceMap");
                    Global.virtualModel.add(ResourceFactory.createStatement(newLDPResource,propertyRM,compiledRM));



                    String graphQuery = resourceMap.getGraphQuery();
                    graphQuery = graphQuery.replace("?res", "<" + currentResourceIRI + ">");
                    Property cgqProperty = ResourceFactory.createProperty(Global.vocabularyPrefix + "compiledGraphQuery");

                    //expand the prefixes
                    Query gq = QueryFactory.create(Global.prefixes + graphQuery);
                    gq.setPrefixMapping(Global.prefixMap);
                    gq.getPrologue().getPrefixMapping().clearNsPrefixMap();
                    graphQuery = gq.serialize();


                    Global.virtualModel.add(ResourceFactory.createStatement(compiledRM,cgqProperty,ResourceFactory.createPlainLiteral(graphQuery)));


                    for (Map.Entry<String, DataSource> datasourceEntry : resourceMap.getDataSources().entrySet()) {
                        dataSource = datasourceEntry.getValue();
                        Resource ds = ResourceFactory.createResource(dataSource.getIRI());
                        Property dsProperty = ResourceFactory.createProperty(Global.vocabularyPrefix + "dataSource");
                        Global.virtualModel.add(ResourceFactory.createStatement(compiledRM,dsProperty,ds));
                        Model dsModel = dataSource.getSelfModel();
                        Global.virtualModel = Global.virtualModel.union(dsModel);
                    }
                }
            }

            genIDts.add(new GenID(newIRI,currentResourceIRI));
        }
        return new EvalResult(genIDts,ldpDD);
    }

    public static class GenID{
        String ldprIRI;
        String rdfResourceIRI;
        public GenID(String ldprIRI, String rdfResourceIRI) {
            this.ldprIRI = ldprIRI;
            this.rdfResourceIRI = rdfResourceIRI;
        }
        public String getLdprIRI() {
            return ldprIRI;
        }
        public void setLdprIRI(String ldprIRI) {
            this.ldprIRI = ldprIRI;
        }
        public String getRdfResourceIRI() {
            return rdfResourceIRI;
        }
        public void setRdfResourceIRI(String rdfResourceIRI) {
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
