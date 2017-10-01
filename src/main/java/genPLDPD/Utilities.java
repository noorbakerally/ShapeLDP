package genPLDPD;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.Iterator;

/**
 * Created by noor on 30/09/17.
 */
public class Utilities {

    public static Dataset cloneDataset(Dataset ds){
        Dataset df = DatasetFactory.create();
        Iterator<String> names = ds.listNames();
        while (names.hasNext()){
            String name = names.next();
            df.addNamedModel(name,ds.getNamedModel(name));
        }
        return df;
    }
    public static Dataset mergeDataSet(Dataset d1,Dataset d2){
        Dataset dsf = DatasetFactory.create();
        Dataset ds1 = cloneDataset(d1);
        Dataset ds2 = cloneDataset(d2);
        Iterator<String> ds1Iterator = ds1.listNames();
        while (ds1Iterator.hasNext()){
            String name = ds1Iterator.next();
            if (ds2.containsNamedModel(name)){
                Model model = ModelFactory.createUnion(ds1.getNamedModel(name),ds2.getNamedModel(name));
                dsf.addNamedModel(name,model);
                ds2.removeNamedModel(name);
            } else {
                dsf.addNamedModel(name,ds1.getNamedModel(name));
            }
        }
        Iterator<String> ds2Iterator = ds2.listNames();
        while (ds2Iterator.hasNext()){
            String name = ds2Iterator.next();
            dsf.addNamedModel(name,ds2.getNamedModel(name));
        }
        return dsf;
    }

    public static String genIRI(String base, String res){
        return base+"?IRI="+res;
    }
}
