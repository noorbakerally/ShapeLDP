package com.github.blazeldp.ldp;

/**
 * Created by noor on 01/10/17.
 */
public class Container extends LDPResource {

    public Container(String IRI) {
        super(IRI);
        this.rtype = "http://www.w3.org/2012/ldp#BasicContainer";
    }


}
