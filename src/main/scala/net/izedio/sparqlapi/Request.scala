package net.izedio.sparqlapi

import java.net.URI

import com.google.common.base.Preconditions
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder

/**
 * Request contains all needed information to create HTTP messages
 * which may be sent to execute a SPARQL query on a remote SPARQL server
 * listening to the HTTP.
 */
trait Request extends MethodDetails {
  val method: Method;
  val sparql: String;
  val mappings: Map[String, URI];
  val graphURIs: List[URI];
  val namedGraphURIs: List[URI];

  val queryParamName: String;
  val graphURIParamName: String;
  val namedGraphURIParamName: String;
  val contentType: ContentType;

  /**
   * Creates an HTTP message instance.
   *
   * @param uri - id of the database resource on the SPARQL server.
   * @return - new initialised HTTP message instance.
   */
  def createHTTPMessage(uri: URI): HttpUriRequest = {
    val builder = new URIBuilder(uri);

    val entity = this.method.createHTTPEntity(this)

    this.method.addQueryParam(builder, this);
    this.method.addGraphURIQueryParams(builder, this);

    val httpMethod = this.method.createHTTPMethod(builder.build());
    httpMethod.setEntity(entity);

    this.addHeaders(httpMethod);

    return httpMethod.httpMessage;
  }

  /**
   * Adds required headers to the HTTP message.
   * Which heads to add is defined by the concrete implementations
   * of this trait.
   *
   * @param httpMethod - HTTP message to add the headers.
   */
  protected def addHeaders(httpMethod: HTTPMethod) {
  }
}

/**
 * Update request is used to send SPARQL queries which modify the data on the server.
 *
 * @param method - HTTP method to use.
 * @param sparql - SPARQL query to send.
 * @param mappings - prefix mappings.
 * @param graphURIs - ids of the graphs that the query uses.
 * @param namedGraphURIs ids of the named graphs that the query uses.
 */
case class UpdateRequest(
  override val method: ExtendedMethod,
  override val sparql: String,
  override val mappings: Map[String, URI] = Map.empty,
  override val graphURIs: List[URI] = List.empty,
  override val namedGraphURIs: List[URI] = List.empty
) extends Request {
  import UpdateRequest._;

  override val contentType = `sparql-update`;
  override val queryParamName = UpdateParamName;
  override val graphURIParamName = GraphURIParamName;
  override val namedGraphURIParamName = NamedGraphURIParamName;
}

private object UpdateRequest {
  val UpdateParamName = "update";
  val GraphURIParamName = "using-graph-uri";
  val NamedGraphURIParamName = "using-named-graph-uri";
}

/**
 * Update request is used to send SPARQL queries which retrive data from the server.
 *
 * @param method - HTTP method to use.
 * @param sparql - SPARQL query to send.
 * @param mappings - prefix mappings.
 * @param accepts - content types of the response that the caller can understand.
 * @param graphURIs - ids of the graphs that the query uses.
 * @param namedGraphURIs ids of the named graphs that the query uses.
 */
case class QueryRequest(
  override val method: Method,
  override val sparql: String,
  override val mappings: Map[String, URI] = Map.empty,
  accepts: List[ContentType],
  override val graphURIs: List[URI] = List.empty,
  override val namedGraphURIs: List[URI] = List.empty
) extends Request {
  import QueryRequest._;

  val contentType = `sparql-query`;
  val queryParamName = QueryParamName;
  val graphURIParamName = GraphURIParamName;
  val namedGraphURIParamName = NamedGraphURIParamName;

  override protected def addHeaders(httpMethod: HTTPMethod) {
    Preconditions.checkArgument(accepts.nonEmpty);

    val accept = accepts.map(ct=>ct.contentType.getMimeType()).mkString(";");
    httpMethod.httpMessage.addHeader(HttpHeaders.ACCEPT, accept);
  }
}

private object QueryRequest {
  val QueryParamName = "query";
  val GraphURIParamName = "default-graph-uri";
  val NamedGraphURIParamName = "named-graph-uri";
}
