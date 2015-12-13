package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.mockito.Mockito
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.io.Source

class MethodSpec extends FlatSpec with Matchers {
  import Mockito._;

  "Get method" should "add query param" in {
    val builder = new URIBuilder();

    val d = mock(classOf[MethodDetails]);
    doReturn("s").when(d).sparql;
    doReturn("q").when(d).queryParamName;

    Get.addQueryParam(builder, d);

    builder.build().toString() shouldBe "?q=s";
  }

  it should "add graph URI params" in {
    val builder = new URIBuilder();

    val d = mock(classOf[MethodDetails]);
    doReturn("ng").when(d).namedGraphURIParamName;
    doReturn("g").when(d).graphURIParamName
    doReturn(List(new URI("urn:x"), new URI("urn:y"))).when(d).graphURIs
    doReturn(List(new URI("urn:a"), new URI("urn:b"))).when(d).namedGraphURIs

    Get.addGraphURIQueryParams(builder, d);

    builder.build().toString() shouldBe "?g=urn%3Ax&g=urn%3Ay&ng=urn%3Aa&ng=urn%3Ab";
  }

  it should "create get HTTP method" in {
    val uri = new URI("urn:a");
    val m = Get.createHTTPMethod(uri);

    m.httpMessage shouldBe a[HttpGet];
    m.httpMessage.getURI shouldBe uri;
  }

  it should "return none when creating entities" in {
    val d = mock(classOf[MethodDetails]);
    Get.createHTTPEntity(d) shouldBe None;
  }

  "Post method" should "not add query param" in {
    val builder = new URIBuilder("urn:a");

    val d = mock(classOf[MethodDetails]);
    doReturn("s").when(d).sparql;
    doReturn("q").when(d).queryParamName;

    Post.addQueryParam(builder, d);

    builder.build().toString() shouldBe "urn:a";
  }

  it should "add graph URI params" in {
    val builder = new URIBuilder();

    val d = mock(classOf[MethodDetails]);
    doReturn("ng").when(d).namedGraphURIParamName;
    doReturn("g").when(d).graphURIParamName
    doReturn(List(new URI("urn:x"), new URI("urn:y"))).when(d).graphURIs
    doReturn(List(new URI("urn:a"), new URI("urn:b"))).when(d).namedGraphURIs

    Post.addGraphURIQueryParams(builder, d);

    builder.build().toString() shouldBe "?g=urn%3Ax&g=urn%3Ay&ng=urn%3Aa&ng=urn%3Ab";
  }

  it should "create post HTTP method" in {
    val uri = new URI("urn:a");
    val m = Post.createHTTPMethod(uri);

    m.httpMessage shouldBe a[HttpPost];
    m.httpMessage.getURI shouldBe uri;
  }

  it should "create HTTP entity" in {
    val d = mock(classOf[MethodDetails]);

    doReturn(`x-trig`).when(d).contentType
    doReturn("s").when(d).sparql;

    val e: StringEntity = Post.createHTTPEntity(d).get;

    e.getContentType().getValue() shouldBe "application/x-trig";
    Source.fromInputStream(e.getContent).getLines().toList shouldBe List("s");
  }

  "FormPost method" should "not add query param" in {
    val builder = new URIBuilder("urn:a");

    val d = mock(classOf[MethodDetails]);

    FormPost.addQueryParam(builder, d);

    builder.build().toString() shouldBe "urn:a";
  }

  it should "not add graph URI params" in {
    val builder = new URIBuilder("urn:a");

    val d = mock(classOf[MethodDetails]);

    FormPost.addGraphURIQueryParams(builder, d);

    builder.build().toString() shouldBe "urn:a";
  }

  it should "create post HTTP method" in {
    val uri = new URI("urn:a");
    val m = FormPost.createHTTPMethod(uri);

    m.httpMessage shouldBe a[HttpPost];
    m.httpMessage.getURI shouldBe uri;
  }

  it should "create HTTP entity" in {
    val d = mock(classOf[MethodDetails]);

    doReturn("s").when(d).sparql;
    doReturn("p").when(d).queryParamName;
    doReturn("ng").when(d).namedGraphURIParamName;
    doReturn("g").when(d).graphURIParamName
    doReturn(List(new URI("urn:x"), new URI("urn:y"))).when(d).graphURIs
    doReturn(List(new URI("urn:a"), new URI("urn:b"))).when(d).namedGraphURIs

    val e: UrlEncodedFormEntity = FormPost.createHTTPEntity(d).get;
    e.getContentType().getValue() shouldBe "application/x-www-form-urlencoded";
    Source.fromInputStream(e.getContent).getLines().toList shouldBe List("p=s&g=urn%3Ax&g=urn%3Ay&ng=urn%3Aa&ng=urn%3Ab");
  }
}
