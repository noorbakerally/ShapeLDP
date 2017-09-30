package loader.configuration;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;

import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class Global {
    public enum ContainerType {Basic,Direct,Indirect}
    public static PrefixMapping prefixMap;
    public static String prefixes = "";
    public static String vocabularyPrefix = "http://opensensingcity.emse.fr/LDPDesignVocabulary/";

    public static String  geniri(){
        String iri = null;
        return iri;
    }


    public static ResultSet exeQuery(String queryStr, Model model){
        queryStr = Global.prefixes + queryStr;
        //System.out.println(queryStr);
        Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet rs = qexec.execSelect() ;
        return rs;
    }
    public static String getVTerm(String lname){
        return vocabularyPrefix+lname;
    }


}
