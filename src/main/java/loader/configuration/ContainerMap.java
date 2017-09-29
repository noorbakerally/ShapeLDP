package loader.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class ContainerMap {
    Map<String,ContainerMap> containerMaps = new HashMap<String, ContainerMap>();
    Map<String,NonContainerMap> nonContainerMaps = new HashMap<String, NonContainerMap>();
    Map<String,ResourceMap> resourceMaps = new HashMap<String, ResourceMap>();
    Global.ContainerType containerType;

}
