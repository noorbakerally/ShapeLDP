# ShapeLDP

ShapeLDP generates <a href="http://opensensingcity.emse.fr/ldpdl/ldpdl.html#ldp-dataset">LDP dataset</a> from data sources using a design document as input. The design document is written in <a href="opensensingcity.emse.fr/ldpdl/ldpdl.html">LDP-DL</a>. Use `mvn clean install` to build the project. There is a command line interface having the following options:

```
 -d,--designDocument <arg>    Path to design document
 -h,--help                    Show help
 -if,--inputFile <arg>        URL for a file as the main input source
 -l                           Disable logging, by default logging is
                              enabled
 -lf,--liftingRule <arg>      lifting rule for main input source
 -o,--outputfile <arg>        Path to LDP dataset
 -s,--sourcesDocument <arg>   source document describing the data sources
 -se,--sparqlEndpoint <arg>   URL for a SPARQL endpoint to act as main
                              input source
 -v,--virtualGraph <arg>      Path to virtual graph
```

- When `if` is provided, a default data source is created and assigned to all <a href="http://opensensingcity.emse.fr/ldpdl/ldpdl.html#resourcemap">ResourceMaps</a>

- The source document is a description of the data sources written in LDP-DL

- `se` and `if` are mutually exclusive, when `se` is provided, a default <a href="http://opensensingcity.emse.fr/ldpdl/ldpdl.html#resourcemap">SPARQL DataSource</a> acts as the main data source
