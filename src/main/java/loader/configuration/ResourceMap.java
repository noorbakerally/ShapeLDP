package loader.configuration;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class ResourceMap {
    String IRI;
    String resourceQuery;
    String graphTemplate;
    String graphQuery;
    Map<String,DataSource> dataSources = new HashMap<String, DataSource>();

    public ResourceMap(String IRI) {
        this.IRI = IRI;
    }

    public String getIRI() {
        return IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public String getResourceQuery() {
        return resourceQuery;
    }

    public void setResourceQuery(String resourceQuery) {
        this.resourceQuery = resourceQuery;
    }

    public String getGraphTemplate() {
        return graphTemplate;
    }

    public void setGraphTemplate(String graphTemplate) {
        this.graphTemplate = graphTemplate;
    }

    public String getGraphQuery() {
        return graphQuery;
    }

    public void setGraphQuery(String graphQuery) {
        this.graphQuery = graphQuery;
    }

    public String toString(int level) {
        String str = "";

        String tab= StringUtils.repeat("\t", level);


        String title = "ResourceMap:";

        String titleUnderline = StringUtils.repeat("", title.length());

        str = tab+title + "\n";

        str += tab+"\t\tIRI: "+getIRI()+"\n";
        str += tab+"\t\tGraphTemplate: "+getGraphTemplate()+"\n";
        str += tab+"\t\tResourceQuery: "+getResourceQuery()+"\n";

        //print child container map
        if (dataSources.size() > 0){
            str += tab+"\t\tDataSources: \n";
            for (Map.Entry <String,DataSource> dataSourceEntry:dataSources.entrySet()){
                //str+=dataSourceEntry.getValue().toString(level);
            }
        }
        return str;
    }

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public void addDataSource(DataSource dataSource){
        dataSources.put(dataSource.getIRI(),dataSource);
    }

}
