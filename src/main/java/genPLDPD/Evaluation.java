package genPLDPD;

import loader.configuration.DesignDocument;
import loader.configuration.NonContainerMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;

import java.util.Map;

/**
 * Created by noor on 30/09/17.
 */
public class Evaluation {

    public static Dataset evalDD(DesignDocument dd,String base){
        Dataset ldpDD = DatasetFactory.create();

        Map<String, NonContainerMap> nonContainerMaps = dd.getNonContainerMaps();
        for (Map.Entry <String,NonContainerMap> nonContainerMapEntry:nonContainerMaps.entrySet()){
            String iri = nonContainerMapEntry.getKey();
            NonContainerMap nonContainerMap = nonContainerMapEntry.getValue();
        }
        return ldpDD;
    }

    public static Dataset evalNM(NonContainerMap nonContainerMap, String contIRI,String pResIRI, Dataset ds){
        Dataset df = DatasetFactory.create();

        return df;
    }
}
