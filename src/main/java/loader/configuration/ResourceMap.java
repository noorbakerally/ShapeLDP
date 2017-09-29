package loader.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noor on 29/09/17.
 */
public class ResourceMap {
    String resourceQuery;
    String graphQuery;
    Map<String,DataSource> dataSources = new HashMap<String, DataSource>();
}
