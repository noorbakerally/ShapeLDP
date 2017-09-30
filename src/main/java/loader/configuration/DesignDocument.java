package loader.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class DesignDocument {
    Map<String,ContainerMap> containerMaps = new HashMap<String, ContainerMap>();
    Map<String,NonContainerMap> nonContainerMaps = new HashMap<String, NonContainerMap>();

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
