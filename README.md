# SPARQL API

This library defines a very simple API which allows to execute SPARQL queries on a remote triple storage
implementing [SPARQL 1.1 Protocol](http://www.w3.org/TR/sparql11-protocol/)

The JAR also contains a default implementation.

# Example

```scala
val httpClient = HttpClients.custom().build();
val result = try {
  DefaultClient(
    httpClient,
    new URI("http://localhost:8890/sparql"),
    QueryRequest(
      FormPost, "select * where {?s ?p ?o}", Map(),
      List(`rdf+xml`),
      List(new URI("http://example.com"))
    )
  )
} finally {
  httpClient.close();
}
````

# Notes

This library supports **query** and **update** operations, allows to use *GET*, *POST* and *URL-encoded POST* methods.

PREFIX mapping is not yet supported, thus for now the queries should include PREFIX definitions. net.izedio.sparqlapi.Request.mappings is ignored.

# How to build
You need to have ant and ivy installed

*ant retrieve package*

This will produce a jar in the target folder.