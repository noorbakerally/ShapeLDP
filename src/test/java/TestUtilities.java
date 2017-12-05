import java.io.File;

/**
 * Created by bakerally on 11/21/17.
 */
public class TestUtilities {

    public static File getTestResource(String className, String resourceName){
        File file = new File(TestUtilities.class.getClassLoader().getResource(className+"/"+resourceName).getFile());
        return file;
    }

    public static String getTestResourceAbsPath(String className, String resourceName){
       return TestUtilities.class.getClassLoader().getResource(className+"/"+resourceName).toString();
    }
}
