package loader.configuration;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class NonContainerMap implements HasResourceMap{
    String IRI;
    Map<String,ResourceMap> resourceMaps = new HashMap<String, ResourceMap>();

    public NonContainerMap(String IRI) {
        this.IRI = IRI;
    }

    public String getIRI() {
        return IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public void addResourceMap(ResourceMap resourceMap) {
        resourceMaps.put(resourceMap.getIRI(),resourceMap);
    }
    public ResourceMap getResourceMap(String currentRMIRI) {
        return resourceMaps.get(currentRMIRI);
    }
    public String toString(int level) {
        String str = "";
        String tab= StringUtils.repeat("\t", level);
        String title = "NonContainerMap:";
        String titleUnderline = StringUtils.repeat("", title.length());
        str = tab+title + "\n";
        str += tab+"\t\tIRI: "+getIRI()+"\n";
        for (Map.Entry <String,ResourceMap> resourceMapEntry:resourceMaps.entrySet()){
            str = str + "\n"+resourceMapEntry.getValue().toString(level+2);
        }
        return str;
    }


    public Map<String, ResourceMap> getResourceMaps() {
        return resourceMaps;
    }

    public void setResourceMaps(Map<String, ResourceMap> resourceMaps) {
        this.resourceMaps = resourceMaps;
    }
}
