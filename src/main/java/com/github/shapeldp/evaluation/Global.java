package com.github.shapeldp.evaluation;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;

/**
 * Created by noor on 29/09/17.
 */
public class Global {
    static String base = "";
    public static boolean physical = true;
    public static int resourceMapsExecuted;
    public enum ContainerType {Basic,Direct,Indirect}
    public static PrefixMapping prefixMap;
    public static PrefixMapping dupPrefixMap;
    public static String prefixes = "";
    public static String vocabularyPrefix = "http://opensensingcity.emse.fr/LDPDesignVocabulary/";
    public static Model virtualModel = ModelFactory.createDefaultModel();


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

    public static Model exeGraphQuery(String queryStr, Model model){
        queryStr = Global.prefixes + queryStr;
        //System.out.println(queryStr);
        Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        return qexec.execConstruct();
    }

    public enum LDPRType {Basic,Direct,Indirect,RDFSource}

    public static String getLDPRTypeIRI(LDPRType ldprType){
        String iri = null;
        if (ldprType == LDPRType.Basic){
            iri = "http://www.w3.org/ns/ldp#BasicContainer";
        }
        if (ldprType == LDPRType.RDFSource){
            iri = "http://www.w3.org/ns/ldp#RDFSource";
        }
        return iri;
    }
}
