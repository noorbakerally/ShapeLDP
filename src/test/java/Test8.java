import geniri.iri.GenIRI;
import geniri.iri.ParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by noor on 24/11/17.
 */
public class Test8 {
    @Test
    public void testExpression () {
        String expr = "ppath(res.graph,'bbb')";
        ByteArrayInputStream bais = new ByteArrayInputStream(expr.getBytes());
        System.setIn(bais);
        GenIRI  iriParser;
        iriParser = new GenIRI(System.in);

        Model model = ModelFactory.createDefaultModel();
        String processedExpr = null;
        try {
            System.out.println(iriParser.expr(model));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
