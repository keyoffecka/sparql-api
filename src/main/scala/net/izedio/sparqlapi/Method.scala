package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.HttpEntity
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConversions

/**
 * Various parameters needed to initialise or build objects
 * managed by the Method implementations.
 */
trait MethodDetails {
  val sparql: String;
  val graphURIs: List[URI];
  val namedGraphURIs: List[URI];
  val contentType: ContentType;
  val queryParamName: String;
  val graphURIParamName: String;
  val namedGraphURIParamName: String;
}

/**
 * Method implementations should be able to prepare resource ids,
 * create underlying HTTP method objects and initialise the objects
 * with http entity instances (if supported).
 */
trait Method {
  /**
   * Appends 'query' parameter to the URI if the method supports the parameter;
   * does nothing if the parameter is not supported.
   *
   * @param builder - URI builder to use to add the parameter.
   * @param details - contains the query parameter name and the parameter value.
   */
  def addQueryParam(builder: URIBuilder, details: MethodDetails) {}

  /**
   * Appends graph URI parameters to the URI if the method supports them;
   * does nothing if the parameters are not supported.
   *
   * @param builder - URI builder to use to add the parameters.
   * @param details - contains the query parameter names and the parameter values.
   */
  def addGraphURIQueryParams(builder: URIBuilder, details: MethodDetails) {}

  /**
   * Creates an underlying HTTP method.
   *
   * @param uri - resource id to pass to the new instances of the underlying http method.
   * @return new instance of the underlying HTTP method.
   */
  def createHTTPMethod(uri: URI): HTTPMethod;

  /**
   * Creates and initialises of the http entity of the underlying http method.
   *
   * @param details - parameters to use to initialize the http entity.
   * @return optional value of new instance of the http entity;
   *         none is returned if the method doesn't support http entities.
   */
  def createHTTPEntity(details: MethodDetails): Option[HttpEntity] = None;
}

/**
 * A marker trait indicating extended methods.
 */
trait ExtendedMethod extends Method;

/**
 * Mixin that is able to add a query parameter to the URI.
 */
trait HasQueryParam {
  /**
   * Adds a query parameter to the URI.
   *
   * @param builder - builder to use to add the query parameter to the URI.
   * @param details - contains the query parameter name and its value.
   */
  def addQueryParam(builder: URIBuilder, details: MethodDetails) {
    builder.addParameter(details.queryParamName, details.sparql);
  }
}

/**
 * Mixin that is able to add graphURI parameters to the URI.
 */
trait HasGraphURIsInQueryParams {
  /**
   * Adds graph URI parameters to the URI.
   *
   * @param builder - builder to use to add the graph URI parameters to the URI.
   * @param details - contains the graph URI parameter names and their values.
   */
  def addGraphURIQueryParams(builder: URIBuilder, details: MethodDetails) {
    this.addURIs(builder, details.graphURIs, details.graphURIParamName)
    this.addURIs(builder, details.namedGraphURIs, details.namedGraphURIParamName)
  }

  /**
   * Adds query parameters.
   *
   * @param builder - builder to use to add the query parameters.
   * @param uris - query parameter values.
   * @param paramName - query parameter name.
   */
  private def addURIs(builder: URIBuilder, uris: List[URI], paramName: String) {
    uris.foreach(uri => {
      builder.addParameter(paramName, uri.toString())
    })
  }
}

/**
 * A mixin which is able to create a Get HTTP method wrapper.
 */
trait CanCreateHTTPGetMessage {
  /**
   * Creates a Get HTTP method wrapper.
   *
   * @param uri - resource id to which the get request will be sent.
   * @return Get HTTP method wrapper.
   */
  def createHTTPMethod(uri: URI): HTTPMethod = new GetHTTPMethod(uri);
}

/**
 * A mixin which is able to create a Post HTTP method wrapper.
 */
trait CanCreateHTTPPostMessage {
  /**
   * Creates a Post HTTP method wrapper.
   *
   * @param uri - resource id to which the post request will be sent.
   * @return Port HTTP method wrapper.
   */
  def createHTTPMethod(uri: URI): HTTPMethod = new PostHTTPMethod(uri);
}

/**
 * Get method is used to send requests to a SPARQL server
 * when all the parameters of the request a passed as the
 * query parameters of the URI.
 */
object Get extends Method
           with HasQueryParam
           with HasGraphURIsInQueryParams
           with CanCreateHTTPGetMessage
{
  override def addGraphURIQueryParams(builder: URIBuilder, details: MethodDetails) {super.addGraphURIQueryParams(builder, details);}

  override def addQueryParam(builder: URIBuilder, details: MethodDetails) {super.addQueryParam(builder, details);}
}

/**
 * Post method is used when the query is passed as a post body;
 * but other parameters are added to the URI.
 */
object Post extends ExtendedMethod
            with HasGraphURIsInQueryParams
            with CanCreateHTTPPostMessage
{
  override def addGraphURIQueryParams(builder: URIBuilder, details: MethodDetails) {super.addGraphURIQueryParams(builder, details);}

  override def createHTTPEntity(
    entityDetails: MethodDetails
  ) = Option(new StringEntity(entityDetails.sparql, entityDetails.contentType.contentType));
}

/**
 * Form Post method is used to pass all the data as a form in the post body.
 */
object FormPost extends ExtendedMethod
                      with CanCreateHTTPPostMessage
{
  import JavaConversions._;

  override def createHTTPEntity(
    entityDetails: MethodDetails
  ) = Option(
    new UrlEncodedFormEntity(
      List(
        new BasicNameValuePair(entityDetails.queryParamName, entityDetails.sparql)
      ) ::: entityDetails.graphURIs.map(
        uri => new BasicNameValuePair(entityDetails.graphURIParamName, uri.toString())
      ) ::: entityDetails.namedGraphURIs.map(
        uri => new BasicNameValuePair(entityDetails.namedGraphURIParamName, uri.toString())
      )
    )
  );
}
