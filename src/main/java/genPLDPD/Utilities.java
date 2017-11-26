package genPLDPD;

import geniri.iri.GenIRI;
import geniri.iri.ParseException;
import ldp.RDFResource;
import loader.configuration.Global;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by noor on 30/09/17.
 */
public class Utilities {

    static GenIRI  iriParser;

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

    public static String processRawQuery(String resourceQuery,List<String> parents){
        int iloc = 0;
        if (resourceQuery.contains("$parent")){
            while (iloc < resourceQuery.length()){
                int floc = resourceQuery.indexOf("$",0);
                int lloc = resourceQuery.indexOf("$",floc+1);
                if (floc > 0){
                    String expr = resourceQuery.substring(floc+1,lloc);
                    expr = expr.replace("\\.","");
                    int expc = StringUtils.countMatches(expr,"parent");
                    String expValue = parents.get(parents.size()-expc);
                    resourceQuery = resourceQuery.substring(0,iloc) +
                            resourceQuery.substring(iloc,floc) +
                            "<"+expValue+">"+resourceQuery.substring(lloc+1,resourceQuery.length());
                } else {
                    iloc = resourceQuery.length() + 1;
                }
            }
        }
        return resourceQuery;
    }

    public static String processIRITemplate(String template, RDFResource resource, List <String> parents){
        int iloc = 0;

        if (template.contains("$")){

            while (iloc < template.length()){
                int floc = template.indexOf("$",0);
                int lloc = template.indexOf("$",floc+1);
                if (floc >= 0){
                    String expr = template.substring(floc+1,lloc);
                    ByteArrayInputStream bais = new ByteArrayInputStream(expr.getBytes());
                    System.setIn(bais);

                    if (iriParser == null){
                        iriParser = new GenIRI(System.in);
                    } else {
                        iriParser.ReInit(System.in);
                    }

                    String processedExpr = null;
                    try {
                        List <Object> arrayList = new ArrayList<Object>();
                        arrayList.add(resource.getModel());
                        arrayList.add(resource.getIRI());
                        arrayList.add(Global.dupPrefixMap);
                        processedExpr = iriParser.expr(arrayList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    template = template.substring(0,iloc) +
                            template.substring(iloc,floc) +
                            processedExpr+template.substring(lloc+1,template.length());
                } else {
                    iloc = resource.getIRI().length() + 1;
                }
            }
        }
        return template;
    }



}
