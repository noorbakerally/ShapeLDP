package loader.configuration;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class ContainerMap implements HasResourceMap {
    String IRI;
    Map<String,ContainerMap> containerMaps = new HashMap<String, ContainerMap>();
    Map<String,NonContainerMap> nonContainerMaps = new HashMap<String, NonContainerMap>();
    Map<String,ResourceMap> resourceMaps = new HashMap<String, ResourceMap>();
    String containerType;
    ContainerMap parentContainerMap;

    public ContainerMap(String containerMapIRI) {
        setIRI(containerMapIRI);
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    public ContainerMap getParentContainerMap() {
        return parentContainerMap;
    }

    public void setParentContainerMap(ContainerMap parentContainerMap) {
        this.parentContainerMap = parentContainerMap;
    }

    public String getIRI() {
        return IRI;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public void addResourceMap(ResourceMap resourceMap) {
        resourceMaps.put(resourceMap.getIRI(),resourceMap);
    }

    public ResourceMap getResourceMap(String currentRMIRI) {
        return resourceMaps.get(currentRMIRI);
    }

    public void addNonContainerMap(NonContainerMap nonContainerMap){
        nonContainerMaps.put(nonContainerMap.getIRI(),nonContainerMap);
        nonContainerMap.setParentContainerMap(this);
    }

    public void addContainerMap(ContainerMap containerMap){
        containerMaps.put(containerMap.getIRI(),containerMap);
        containerMap.setParentContainerMap(this);
    }

    public String toString(int level){
        String str = "";
        String tab= StringUtils.repeat("\t", level);

        str += tab+"\t\tContainerType: "+getContainerType()+"\n";
        for (Map.Entry <String,NonContainerMap> nonContainerMaps:nonContainerMaps.entrySet()){
            str = str + "\n"+nonContainerMaps.getValue().toString(level+2);
        }

        //print child container map
        if (containerMaps.size() > 0){
            str += tab+"\t\tChild Containers: \n";
            for (Map.Entry <String,ContainerMap> containerMapEntry:containerMaps.entrySet()){
                str+=containerMapEntry.getValue().toString(level+4);
            }
        }
        return str;
    }

    public Map<String, ResourceMap> getResourceMaps() {
        return resourceMaps;
    }

    public void setResourceMaps(Map<String, ResourceMap> resourceMaps) {
        this.resourceMaps = resourceMaps;
    }
    public Global.LDPRType getType() {
        return Global.LDPRType.Basic;
    }

    public Map<String, ContainerMap> getContainerMaps() {
        return containerMaps;
    }

    public void setContainerMaps(Map<String, ContainerMap> containerMaps) {
        this.containerMaps = containerMaps;
    }

    public Map<String, NonContainerMap> getNonContainerMaps() {
        return nonContainerMaps;
    }

    public void setNonContainerMaps(Map<String, NonContainerMap> nonContainerMaps) {
        this.nonContainerMaps = nonContainerMaps;
    }
}
