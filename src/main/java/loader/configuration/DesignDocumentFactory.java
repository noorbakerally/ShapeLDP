package loader.configuration;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDF;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by noor on 29/09/17.
 */
public class DesignDocumentFactory {
    static DesignDocument designDocument;
    public static Model model;

    private static Map <String,ContainerMap> containerMaps = new HashMap<String, ContainerMap>();
    private static Map <String,NonContainerMap> nonContainerMaps = new HashMap<String, NonContainerMap>();
    private static Map <String,DataSource> dataSourceMaps = new HashMap<String, DataSource>();
    private static  DataSource mainDataSource;



    public static DesignDocument createDDFromFile(String ddLocation, DataSource dataSource){


        designDocument = new DesignDocument();

        //loading the design document into a model
        model = RDFDataMgr.loadModel(ddLocation);

        //setting the default datasource if there is one
        if (dataSource != null){
            mainDataSource = dataSource;
            dataSourceMaps.put(mainDataSource.getIRI(),mainDataSource);
        }

        //getting the defined prefixes from the design document
        Global.prefixMap = PrefixMapping.Factory.create();
        Global.prefixMap.setNsPrefixes(model.getNsPrefixMap());
        for (Map.Entry <String,String> prefix: Global.prefixMap.getNsPrefixMap().entrySet()){
            String key = prefix.getKey();
            key = key + ":";
            String value = prefix.getValue();
            Global.prefixes = "PREFIX "+key+" <"+value+">\n" + Global.prefixes;
        }


        //load all container maps in the temporary list
        String containerMapQuery="SELECT DISTINCT ?containerMap " +
                "WHERE { ?containerMap a :ContainerMap .}";
        ResultSet containerMapRs = Global.exeQuery(containerMapQuery, model);
        while (containerMapRs.hasNext()){
            String containerMapIRI = containerMapRs.next().get("?containerMap").toString();
            ContainerMap containerMap = new ContainerMap(containerMapIRI);
            containerMaps.put(containerMapIRI,containerMap);
        }

        //load all null container maps in the temporary list
        String ncontainerMapQuery="SELECT DISTINCT ?containerMap " +
                "WHERE { ?containerMap a :NullContainerMap .}";
        ResultSet ncontainerMapRs = Global.exeQuery(ncontainerMapQuery, model);
        while (ncontainerMapRs.hasNext()){

            //set null container as container map
            String containerMapIRI = ncontainerMapRs.next().get("?containerMap").toString();
            String containerMapClassIRI = Global.prefixMap.getNsPrefixURI("")+"ContainerMap";
            model = model.add(ResourceFactory.createResource(containerMapIRI), RDF.type,ResourceFactory.createResource(containerMapClassIRI));
            ContainerMap containerMap = new ContainerMap(containerMapIRI);


            //create the null resource map
            ResourceMap nullResourceMap = new ResourceMap("nullResourceMap");
            nullResourceMap.setResourceQuery("{VALUES (?res) {(UNDEF)}}");
            DataSource nullDataSource = new RDFFileDataSource("null");
            nullDataSource.setModel(ModelFactory.createDefaultModel());
            nullResourceMap.addDataSource(nullDataSource);


            containerMap.addResourceMap(nullResourceMap);

            containerMaps.put(containerMapIRI,containerMap);

        }


        //load all non container maps in the temporary list
        String nonContainerMapQuery="SELECT DISTINCT ?nonContainerMap " +
                "WHERE { ?nonContainerMap a :NonContainerMap .}";
        ResultSet nonContainerMapRs = Global.exeQuery(nonContainerMapQuery, model);
        while (nonContainerMapRs.hasNext()){
            String nonContainerMapIRI = nonContainerMapRs.next().get("?nonContainerMap").toString();
            NonContainerMap nonContainerMap = new NonContainerMap(nonContainerMapIRI);
            nonContainerMaps.put(nonContainerMapIRI,nonContainerMap);

            //load the nonContainer map
            loadNonContainerMap(nonContainerMap);
        }


        setContainerMaps();
        setNonContainerMaps();

        //load container map objects
        for (Map.Entry <String,ContainerMap> containerMapEntry:containerMaps.entrySet()){
            ContainerMap containerMap = containerMapEntry.getValue();
            loadContainerMap(containerMap);
            if (containerMap.getParentContainerMap() == null){
                designDocument.addContainerMap(containerMap);
            }

        }

        for (Map.Entry <String,NonContainerMap> nonContainerMapEntry:nonContainerMaps.entrySet()){
            NonContainerMap nonContainerMap = nonContainerMapEntry.getValue();
            if (nonContainerMap.getParentContainerMap() == null){
                designDocument.addNonContainerMap(nonContainerMap);
            }

        }

        setContainerMapAttributes();
        setNonContainerMapAttributes();


        //loading all datasources
        for (Map.Entry <String,DataSource> dataSourceEntry:dataSourceMaps.entrySet()){
            dataSourceEntry.getValue().load();
        }

        return designDocument;
    }

    static void setContainerMapAttributes(){
        String ResourceMapQuery = "SELECT DISTINCT * \n" +
                "WHERE { ?sourceMap a :ContainerMap . " +
                "?sourceMap ?p ?o ." +
                "}";

        ResultSet rs = Global.exeQuery(ResourceMapQuery, model);
        //System.out.println(ResultSetFormatter.asText(rs));
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String containerMapIRI = qs.get("?sourceMap").toString();
            String p = qs.get("?p").toString();
            String o = qs.get("?o").toString();

            if (Global.getVTerm("iriTemplate").equals(p)) {
                containerMaps.get(containerMapIRI).setIriTemplate(o);
            }
        }
    }

    static void setNonContainerMapAttributes(){
        String ResourceMapQuery = "SELECT DISTINCT * \n" +
                "WHERE { ?sourceMap a :NonContainerMap . " +
                "?sourceMap ?p ?o ." +
                "}";

        ResultSet rs = Global.exeQuery(ResourceMapQuery, model);
        //System.out.println(ResultSetFormatter.asText(rs));
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String nonContainerMapIRI = qs.get("?sourceMap").toString();
            String p = qs.get("?p").toString();
            String o = qs.get("?o").toString();

            if (Global.getVTerm("iriTemplate").equals(p)) {
                nonContainerMaps.get(nonContainerMapIRI).setIriTemplate(o);
            }
        }
    }


    static void loadContainerMap(ContainerMap containerMap){
        setContainerType(containerMap);
        setResourceMaps(containerMap);

        //load resource map datasources
        for (Map.Entry <String, ResourceMap> resourceMapEntry:containerMap.getResourceMaps().entrySet()){
            ResourceMap resourceMap = resourceMapEntry.getValue();
            setDataSources(resourceMap);
        }

    }

    static void loadNonContainerMap(NonContainerMap nonContainerMap){
        setResourceMaps(nonContainerMap);

        //load resource map datasources
        for (Map.Entry <String, ResourceMap> resourceMapEntry:nonContainerMap.getResourceMaps().entrySet()){
            ResourceMap resourceMap = resourceMapEntry.getValue();
            setDataSources(resourceMap);
        }
    }

    static void setContainerMaps(){
        String containerMapQuery = "SELECT DISTINCT * \n" +
                "WHERE { ?parentIRI a :ContainerMap . ?childIRI a :ContainerMap . " +
                "?parentIRI :containerMap ?childIRI ." +
                "}";
        ResultSet rs = Global.exeQuery(containerMapQuery, model);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String parent = qs.get("?parentIRI").toString();
            String child = qs.get("?childIRI").toString();
            containerMaps.get(parent).addContainerMap(containerMaps.get(child));
        }
    }

    static void setNonContainerMaps(){
        String nonContainerMapQuery = "SELECT DISTINCT * \n" +
                "WHERE { ?nonContainerMapIRI a :NonContainerMap . " +
                "?containerMapIRI :nonContainerMap ?nonContainerMapIRI ." +
                "}";
        ResultSet rs = Global.exeQuery(nonContainerMapQuery, model);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String nonContainerMapIRI = qs.get("?nonContainerMapIRI").toString();
            String containerMapIRI = qs.get("?containerMapIRI").toString();
            containerMaps.get(containerMapIRI).addNonContainerMap(nonContainerMaps.get(nonContainerMapIRI));
        }
    }



    private static void setContainerType(ContainerMap containerMap) {
        String containerTypeQuery="SELECT DISTINCT * \n" +
                "WHERE { " +
                    "<containerMapIRI> :containerType ?containerType ." +
                "}";
        containerTypeQuery = containerTypeQuery.replace("containerMapIRI",containerMap.getIRI());
        ResultSet rs = Global.exeQuery(containerTypeQuery, model);
        while (rs.hasNext()){
            String containerType = rs.next().get("?containerType").toString();
            containerMap.containerType = containerType;
        }
    }


    static void setDataSources(ResourceMap resourceMap){
        String dataSourceQuery = "SELECT DISTINCT * \n" +
                "WHERE { " +
                "<"+resourceMap.getIRI()+"> :dataSource ?dataSourceIRI . "+
                "?dataSourceIRI ?p ?o ." +
                "?dataSourceIRI a ?type ." +
                "}";
        ResultSet rs = Global.exeQuery(dataSourceQuery, model);
        //System.out.println(ResultSetFormatter.asText(rs));

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String p = qs.get("?p").toString();
            String o = qs.get("?o").toString();
            String type = qs.get("?type").toString();
            String dataSourceIRI = qs.get("?dataSourceIRI").toString();

            DataSource dataSource = resourceMap.getDataSources().get(dataSourceIRI);

            if (dataSource == null){
                if (Global.getVTerm("SPARQLDataSource").equals(type)){
                    dataSource = new SPARQLDataSource(dataSourceIRI);
                } else {
                    dataSource = new RDFFileDataSource(dataSourceIRI);
                }
                resourceMap.getDataSources().put(dataSourceIRI,dataSource);
            }

            if (Global.getVTerm("location").equals(p)) {
                dataSource.setLocation(o);
            }

            if (Global.getVTerm("liftingRule").equals(p)) {
                ((RDFFileDataSource)dataSource).setLiftingRuleLocation(o);
            }
            dataSourceMaps.put(dataSource.getIRI(),dataSource);
        }

    }
    static void setResourceMaps(HasResourceMap sourceMap){
        String ResourceMapQuery = "SELECT DISTINCT * \n" +
                "WHERE { <"+sourceMap.getIRI()+"> a ?sourceMapType . " +
                "<"+sourceMap.getIRI()+"> :resourceMap ?resourceMapIRI ." +
                "?resourceMapIRI ?p ?o ." +
                "}";
        ResultSet rs = Global.exeQuery(ResourceMapQuery, model);
        //System.out.println(ResultSetFormatter.asText(rs));
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            String sourceMapType = qs.get("?sourceMapType").toString();
            String resourceMapIRI = qs.get("?resourceMapIRI").toString();
            String p = qs.get("?p").toString();
            String o = qs.get("?o").toString();

            ResourceMap resourceMap = sourceMap.getResourceMap(resourceMapIRI);
            //check if resource map exist else create it
            if (resourceMap == null){
                resourceMap = new ResourceMap(resourceMapIRI);
            }

            if (Global.getVTerm("graphTemplate").equals(p)) {
                resourceMap.setGraphTemplate(o);
            }
            if (Global.getVTerm("graphQuery").equals(p)) {
                resourceMap.setGraphQuery(o);
            }
            if (Global.getVTerm("resourceQuery").equals(p)) {
                resourceMap.setResourceQuery(o);
            }


            if (mainDataSource !=null){
                if (!resourceMap.dataSources.containsKey("DefaulDataSource")){
                    resourceMap.dataSources.clear();
                    resourceMap.addDataSource(mainDataSource);
                }
            }

            if (containerMaps.containsKey(sourceMap.getIRI())){
                containerMaps.get(sourceMap.getIRI()).addResourceMap(resourceMap);
            }

            if (nonContainerMaps.containsKey(sourceMap.getIRI())){
                nonContainerMaps.get(sourceMap.getIRI()).addResourceMap(resourceMap);
            }
        }


    }


    public static Map<String, ContainerMap> getContainerMaps() {
        return containerMaps;
    }

    public static void setContainerMaps(Map<String, ContainerMap> containerMaps) {
        DesignDocumentFactory.containerMaps = containerMaps;
    }


}
