package ldp;

/**
 * Created by noor on 01/10/17.
 */
public abstract class LDPResource extends RDFResource{
    Container container;
    RDFResource relatedResource;
    String rtype;
    public LDPResource(String IRI) {
        super(IRI);
    }




}
