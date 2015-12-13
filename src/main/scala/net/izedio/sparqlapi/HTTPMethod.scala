package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest

/**
 * Implementations wrap HttpUriRequest.
 * If the wrapped class hasn't the setEntity method
 * the implementation just does nothing.
 */
trait HTTPMethod {
  def httpMessage: HttpUriRequest;

  /**
   * Sets an entity to the underlying http request.
   * Or does nothing if the underlying http request doesn't support entity setting.
   *
   * @param entity - entity to set.
   */
  def setEntity(entity: Option[HttpEntity]) {
  }
}

/**
 * Wraps HttpGet which doesn't allow entity setting.
 *
 * @param uri - resource id to where the request will be sent.
 */
case class GetHTTPMethod(uri: URI) extends HTTPMethod {
  override val httpMessage = new HttpGet(uri)
}

/**
 * Wraps HttpPost which allows entity setting.
 *
 * @param uri - resource id to where the request will be sent.
 */
case class PostHTTPMethod(uri: URI) extends HTTPMethod {
  val httpMessage = new HttpPost(uri)

  override def setEntity(entity: Option[HttpEntity]) {
    this.httpMessage.setEntity(entity.get);
  }
}
