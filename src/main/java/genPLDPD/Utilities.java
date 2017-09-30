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
    public static Dataset mergeDataSet(Dataset ds1,Dataset ds2){
        Dataset dsf = DatasetFactory.create();
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
}
