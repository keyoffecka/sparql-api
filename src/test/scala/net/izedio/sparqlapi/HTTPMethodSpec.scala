package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.mockito.Mockito
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class HTTPMethodSpec extends FlatSpec with Matchers {
  import Mockito._;

  "GetHTTPMethod" should "wrap HttpGet" in {
    val uri = new URI("http://example.com");
    GetHTTPMethod(uri).httpMessage.isInstanceOf[HttpGet] should be (true);
  }

  "PostHTTPMethod" should "wrap HttpPost" in {
    val uri = new URI("http://example.com");
    PostHTTPMethod(uri).httpMessage.isInstanceOf[HttpPost] should be (true);
  }

  it should "set http entity" in {
    val e = mock(classOf[HttpEntity]);
    val eo = Option(e);

    val uri = new URI("http://example.com");
    val m = spy(PostHTTPMethod(uri));

    val hp = mock(classOf[HttpPost]);

    doReturn(hp).when(m).httpMessage;

    m.setEntity(eo);

    verify(hp).setEntity(e);
  }
}
