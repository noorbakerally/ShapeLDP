import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.NonContainerMap;
import tests.*;

import java.io.File;
import java.util.Map;

/**
 * Created by noor on 30/09/17.
 */
public class main {
    public static void main(String [] args){

        /*
        ClassLoader classLoader = main.class.getClassLoader();
        File file = new File(classLoader.getResource("designdocument.ttl").getFile());
        //File file = new File(classLoader.getResource("Fulldd.ttl").getFile());

        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        */

        Test4 t1 = new Test4();


    }
}
