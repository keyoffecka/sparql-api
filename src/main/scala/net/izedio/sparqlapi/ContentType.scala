package net.izedio.sparqlapi

import org.apache.http.entity
import entity.ContentType.create

abstract class ContentType(val contentType: entity.ContentType);
object `*/*` extends ContentType(create("*/*"));
object `x-www-form-urlencoded` extends ContentType(create("application/x-www-form-urlencoded"));
object `sparql-query` extends ContentType(create("application/sparql-query"));
object `sparql-update` extends ContentType(create("application/sparql-update"));
object `rdf+xml` extends ContentType(create("application/rdf+xml"));
object `x-turtle` extends ContentType(create("application/x-turtle"));
object `N-Triples` extends ContentType(create("text/plain"));
object `x-trig` extends ContentType(create("application/x-trig"));
object trix extends ContentType(create("application/trix"));
object `x-nquards` extends ContentType(create("text/x-nquads"));
object `ld+json` extends ContentType(create("application/ld+json"));
object `sparql-results+xml` extends ContentType(create("application/sparql-results+xml"));
object `sparql-results+json` extends ContentType(create("application/sparql-results+json"));
object `x-binary-rdf-results-table` extends ContentType(create("application/x-binary-rdf-results-table"));
object boolean extends ContentType(create("text/boolean"));
