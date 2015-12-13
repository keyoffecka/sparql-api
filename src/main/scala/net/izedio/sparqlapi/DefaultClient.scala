package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient

import scala.io.Source

/**
 * Default implementation of the SPARQL client,
 * which sends a request to the specified URI
 * using the given HTTP client instance.
 */
class DefaultClient extends Client {
  def apply(httpClient: HttpClient, uri: URI, request: Request): String = {
    val httpMessage = request.createHTTPMessage(uri);
    val response = httpClient.execute(httpMessage);

    val contents = Source.fromInputStream(response.getEntity.getContent()).getLines().mkString("\n")
    if (response.getStatusLine.getStatusCode() < HttpStatus.SC_OK
      || response.getStatusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
      throw ClientException(
        response.getStatusLine.getStatusCode(),
        response.getStatusLine.getReasonPhrase(),
        contents
      );
    }

    return contents;
  }
};

object DefaultClient extends DefaultClient;