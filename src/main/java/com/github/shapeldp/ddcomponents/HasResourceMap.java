package com.github.shapeldp.ddcomponents;

import com.github.shapeldp.evaluation.Global;

/**
 * Created by noor on 30/09/17.
 */
public interface HasResourceMap {
    public String getIRI();
    public void addResourceMap(ResourceMap resourceMap);
    public ResourceMap getResourceMap(String IRI);
    public Global.LDPRType getType();
    public String getIRITemplate();
    public String getIRIQueryTemplate();
}

