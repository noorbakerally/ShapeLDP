import com.github.shapeldp.templateIRI.GenIRI;
import com.github.shapeldp.templateIRI.ParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.VCARD;
import org.junit.Assert;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by noor on 24/11/17.
 */
public class Test7 {
    @Test
    public void testExpression () {
        GenIRI  iriParser;

        /***
         * Test Expression 1
         *
         */
        String expr = "ppath(res.graph,'vcard:N/vcard:Given')";
        ByteArrayInputStream bais = new ByteArrayInputStream(expr.getBytes());
        System.setIn(bais);

        iriParser = new GenIRI(System.in);



        Model model = ModelFactory.createDefaultModel();

        String processedExpr = null;
        try {


            Resource johnSmith
                    = model.createResource("http://example.com/p1")
                    .addProperty(VCARD.FN, "Noorani Bakerally")
                    .addProperty(VCARD.N,
                            model.createResource()
                                    .addProperty(VCARD.Given, "Noorani")
                                    .addProperty(VCARD.Family, "Bakerally"));

            //add the model
            List <Object> arrayList = new ArrayList<Object>();
            arrayList.add(model);

            //add the resIRI
            String resIRI = "http://example.com/p1";
            arrayList.add(resIRI);

            //add the prefix map
            PrefixMapping prefixMap = PrefixMapping.Factory.create();
            prefixMap.setNsPrefix("dcat","http://www.w3.org/ns/dcat#");
            prefixMap.setNsPrefix("vcard","http://www.w3.org/2001/vcard-rdf/3.0#");
            prefixMap.setNsPrefix("skos","http://www.w3.org/2001/vcard-rdf/1.0#");
            arrayList.add(prefixMap);
            Assert.assertTrue(iriParser.expr(arrayList).equals("Noorani"));

            //model.write(System.out, "Turtle");

            /***
             * Test Expression 2
             *
             */
            expr = "concat('a','b')";
            bais = new ByteArrayInputStream(expr.getBytes());
            System.setIn(bais);
            iriParser.ReInit(System.in);
            System.out.println( );
            Assert.assertTrue(iriParser.expr(null).equals("ab"));

            /***
             * Test Expression 3
             *
             */
            expr = "concat(ppath(res.graph,'vcard:N/vcard:Given'),'b')";
            bais = new ByteArrayInputStream(expr.getBytes());
            System.setIn(bais);
            iriParser.ReInit(System.in);
            Assert.assertTrue(iriParser.expr(arrayList).equals("Nooranib"));

            /***
             * Test Expression 4
             *
             */
            expr = "replace('Nourani','u','o')";
            bais = new ByteArrayInputStream(expr.getBytes());
            System.setIn(bais);
            iriParser.ReInit(System.in);
            Assert.assertTrue(iriParser.expr(arrayList).equals("Noorani"));


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
