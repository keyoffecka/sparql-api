package net.izedio.sparqlapi.internals

import java.net.URI

import net.izedio.sparqlapi.DefaultClient
import net.izedio.sparqlapi.Get
import net.izedio.sparqlapi.Post
import net.izedio.sparqlapi.QueryRequest
import net.izedio.sparqlapi.FormPost
import net.izedio.sparqlapi.UpdateRequest
import net.izedio.sparqlapi.`rdf+xml`
import net.izedio.sparqlapi.`x-turtle`
import org.apache.http.impl.client.HttpClients

object Main {
  def main(args: Array[String]) {
    val httpClient = HttpClients.custom().build();
    val result = try {
      DefaultClient(
        httpClient,
        new URI("http://localhost:8890/sparql"),
        QueryRequest(
          FormPost, /*"INSERT DATA { <a> <p> <b> }"*/ "select * where {?s ?p ?o}", Map(),
          List(`rdf+xml`),
          List(new URI("http://example.com"))
        )
      )
    } finally {
      httpClient.close();
    }
    println(result);
  }
}
