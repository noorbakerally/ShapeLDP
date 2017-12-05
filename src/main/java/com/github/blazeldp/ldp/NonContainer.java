package com.github.blazeldp.ldp;

/**
 * Created by noor on 01/10/17.
 */
public class NonContainer extends LDPResource {
    public NonContainer(String IRI) {
        super(IRI);
        this.rtype = "http://www.w3.org/2012/ldp#RDFSource";
    }
}
