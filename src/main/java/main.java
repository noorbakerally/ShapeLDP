import genPLDPD.Utilities;
import geniri.iri.GenIRI;
import geniri.iri.ParseException;
import loader.configuration.ContainerMap;
import loader.configuration.DesignDocument;
import loader.configuration.DesignDocumentFactory;
import loader.configuration.NonContainerMap;
import org.apache.commons.lang3.StringUtils;
import tests.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Map;

/**
 * Created by noor on 30/09/17.
 */
public class main {
    public static void main(String [] args) throws ParseException {

        /*
        ClassLoader classLoader = main.class.getClassLoader();
        File file = new File(classLoader.getResource("designdocument.ttl").getFile());
        //File file = new File(classLoader.getResource("Fulldd.ttl").getFile());

        DesignDocument dd = DesignDocumentFactory.createDDFromFile(file.getAbsolutePath());
        Map<String, ContainerMap> containerMaps = dd.getContainerMaps();
        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        */

        //Test12 t1 = new Test12();

        /*String path = "http://www.eriklievaart.com/blog/javacc2.html";
        String str = Utilities.processIRITemplate("test/$path(res,0)$",path,null);
        System.out.println(str);*/

    }
}
