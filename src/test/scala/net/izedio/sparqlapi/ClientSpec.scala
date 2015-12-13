package net.izedio.sparqlapi

import java.io.ByteArrayInputStream
import java.net.URI

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.mockito.Mockito
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ClientSpec extends FlatSpec with Matchers {
  import Mockito._;

  "DefaultClient" should "return server data" in {
    val uri = new URI("http://example.com/db");
    val hur = mock(classOf[HttpUriRequest]);

    val is = new ByteArrayInputStream("result".getBytes("UTF-8"));

    val he = mock(classOf[HttpEntity]);
    doReturn(is).when(he).getContent();

    val sl = mock(classOf[StatusLine]);
    doReturn(HttpStatus.SC_OK).when(sl).getStatusCode();

    val hr = mock(classOf[HttpResponse]);
    doReturn(he).when(hr).getEntity();
    doReturn(sl).when(hr).getStatusLine();

    val c = mock(classOf[HttpClient]);
    doReturn(hr).when(c).execute(hur);

    val r = mock(classOf[Request]);
    doReturn(hur).when(r).createHTTPMessage(uri);

    DefaultClient(c, uri, r) should be ("result");
  }

  it should "throw ClientException" in {
    val uri = new URI("http://example.com/db");
    val hur = mock(classOf[HttpUriRequest]);

    val is = new ByteArrayInputStream("result".getBytes("UTF-8"));

    val he = mock(classOf[HttpEntity]);
    doReturn(is).when(he).getContent();

    val sl = mock(classOf[StatusLine]);
    doReturn(HttpStatus.SC_NOT_FOUND).when(sl).getStatusCode();
    doReturn("reason").when(sl).getReasonPhrase();

    val hr = mock(classOf[HttpResponse]);
    doReturn(he).when(hr).getEntity();
    doReturn(sl).when(hr).getStatusLine();

    val c = mock(classOf[HttpClient]);
    doReturn(hr).when(c).execute(hur);

    val r = mock(classOf[Request]);
    doReturn(hur).when(r).createHTTPMessage(uri);

    try {
      DefaultClient(c, uri, r)
      fail();
    } catch {
      case ClientException(404, "reason", "result") =>
      case _ : Throwable => fail()
    }
  }
}
