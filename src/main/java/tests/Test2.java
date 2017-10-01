package tests;

import genPLDPD.Evaluation;
import ldp.Container;
import loader.configuration.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Created by noor on 01/10/17.
 */
public class Test2 {
    public Test2() {
        //test resource map

        String base = "http://www.example.com";
        Evaluation.base = base;
        Container c1 = new Container(base);
        Dataset ds = DatasetFactory.create();

        ContainerMap cm = new ContainerMap("http://www.example.com/data/CM");
        ResourceMap resourceMap = new ResourceMap("http://www.example.com/data/RM");
        cm.addResourceMap(resourceMap);

        String resourceQuery = "{?res a dcat:Dataset .} LIMIT 1";
        String graphQuery = "CONSTRUCT { ?res ?p ?o . } WHERE {  ?res ?p ?o . }";
        resourceMap.setResourceQuery(resourceQuery);
        resourceMap.setGraphQuery(graphQuery);

        DataSource d1 = new RDFFileDataSource("Da","https://opendata.paris.fr/api/v2/catalog/exports/ttl");
        resourceMap.getDataSources().put("https://opendata.paris.fr/api/v2/catalog/exports/ttl",d1);

        Global.prefixes = "PREFIX dcat: <http://www.w3.org/ns/dcat#>";

        Evaluation.EvalResult evalResult = Evaluation.evalRM(resourceMap,cm, c1, null, ds);
        RDFDataMgr.write(System.out, evalResult.getDs(), Lang.TRIG) ;


    }
}
