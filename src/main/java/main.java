import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by noor on 30/09/17.
 */
public class main {
    public static void main(String [] args){
        ClassLoader classLoader = main.class.getClassLoader();
        File file = new File(classLoader.getResource("designdocument.ttl").getFile());
        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        for (Map.Entry <String,ContainerMap> containerMapEntry:dd.getContainerMaps().entrySet()){
            ContainerMap containerMap = containerMapEntry.getValue();
            if (containerMap.getParentContainerMap() == null){
                containerMap.toString(1);
            }
        }
    }
}
