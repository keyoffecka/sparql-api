package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.client.HttpClient

/**
 * SPARQL Client
 */
trait Client {
  /**
   * Sends an appropriate HTTP request to a SPARQL server to execute a query.
   * Please notice, the client reads all the data from the stream at once;
   * The caller should assume that the amount of the returned data is reasonable.
   *
   * @param httpClient - HTTP client to use to send the HTTP request.
   * @param uri - URI of the database served by the SPARQL server.
   * @param request - request to send.
   * @return text returned by the server.
   * @throws ClientException if the server cannot process the request.
   */
  def apply(httpClient: HttpClient, uri: URI, request: Request): String;
}

/**
 * Thrown if the SPARQL server cannot process a request.
 *
 * @param code - HTTP status code.
 * @param reason - reason explaining the error.
 * @param contents - text data returned by the server.
 */
case class ClientException(
  code: Int,
  reason: String,
  contents: String
) extends Exception {
  override def toString: String = s"${this.getClass().getName} {\n\tcode: ${this.code},\n\treason: ${this.reason},\n\tcontents: ${this.contents}\n}";
}