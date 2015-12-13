package net.izedio.sparqlapi

import java.net.URI

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.io.Source

class RequestSpec extends FlatSpec with Matchers {

  "UpdateRequest" should "create POST message" in {
    val g = List(new URI("urn:integer:1"), new URI("urn:float:1.67"));
    val ng = List(new URI("urn:string:value1"), new URI("urn:boolean:false"));
    val req = UpdateRequest(Post, "SELECT", Map(), g, ng);
    val mes: HttpPost = req.createHTTPMessage(new URI("http://example.com")).asInstanceOf[HttpPost];

    Source.fromInputStream(mes.getEntity.getContent()).mkString shouldBe "SELECT";
    mes.getURI shouldBe new URI("http://example.com?using-graph-uri=urn%3Ainteger%3A1&using-graph-uri=urn%3Afloat%3A1.67&using-named-graph-uri=urn%3Astring%3Avalue1&using-named-graph-uri=urn%3Aboolean%3Afalse");
    mes.getAllHeaders shouldBe Array.empty
  }

  it should "create FORM POST message" in {
    val g = List(new URI("urn:integer:1"), new URI("urn:float:1.67"));
    val ng = List(new URI("urn:string:value1"), new URI("urn:boolean:false"));
    val req = UpdateRequest(FormPost, "SELECT", Map(), g, ng);
    val mes = req.createHTTPMessage(new URI("http://example.com")).asInstanceOf[HttpPost];

    Source.fromInputStream(mes.getEntity.getContent()).mkString shouldBe "update=SELECT&using-graph-uri=urn%3Ainteger%3A1&using-graph-uri=urn%3Afloat%3A1.67&using-named-graph-uri=urn%3Astring%3Avalue1&using-named-graph-uri=urn%3Aboolean%3Afalse";
    mes.getURI shouldBe new URI("http://example.com");
    mes.getAllHeaders shouldBe Array.empty
  }


  "QueryRequest" should "create GET message" in {
    val g = List(new URI("urn:integer:1"), new URI("urn:float:1.67"));
    val ng = List(new URI("urn:string:value1"), new URI("urn:boolean:false"));
    val acc = List(trix, boolean);
    val req = QueryRequest(Get, "SELECT", Map(), acc, g, ng);
    val mes = req.createHTTPMessage(new URI("http://example.com")).asInstanceOf[HttpGet];

    mes.getURI shouldBe new URI("http://example.com?query=SELECT&default-graph-uri=urn%3Ainteger%3A1&default-graph-uri=urn%3Afloat%3A1.67&named-graph-uri=urn%3Astring%3Avalue1&named-graph-uri=urn%3Aboolean%3Afalse");
    mes.getAllHeaders should have size 1
    mes.getAllHeaders()(0).toString shouldBe "Accept: application/trix;text/boolean"
  }

  it should "create POST message" in {
    val g = List(new URI("urn:integer:1"), new URI("urn:float:1.67"));
    val ng = List(new URI("urn:string:value1"), new URI("urn:boolean:false"));
    val acc = List(trix, boolean);
    val req = QueryRequest(Post, "SELECT", Map(), acc, g, ng);
    val mes: HttpPost = req.createHTTPMessage(new URI("http://example.com")).asInstanceOf[HttpPost];

    Source.fromInputStream(mes.getEntity.getContent()).mkString shouldBe "SELECT";
    mes.getURI shouldBe new URI("http://example.com?default-graph-uri=urn%3Ainteger%3A1&default-graph-uri=urn%3Afloat%3A1.67&named-graph-uri=urn%3Astring%3Avalue1&named-graph-uri=urn%3Aboolean%3Afalse");
    mes.getAllHeaders()(0).toString shouldBe "Accept: application/trix;text/boolean"
  }

  it should "create FORM POST message" in {
    val g = List(new URI("urn:integer:1"), new URI("urn:float:1.67"));
    val ng = List(new URI("urn:string:value1"), new URI("urn:boolean:false"));
    val acc = List(trix, boolean);
    val req = QueryRequest(FormPost, "SELECT", Map(), acc, g, ng);
    val mes: HttpPost = req.createHTTPMessage(new URI("http://example.com")).asInstanceOf[HttpPost];

    Source.fromInputStream(mes.getEntity.getContent()).mkString shouldBe "query=SELECT&default-graph-uri=urn%3Ainteger%3A1&default-graph-uri=urn%3Afloat%3A1.67&named-graph-uri=urn%3Astring%3Avalue1&named-graph-uri=urn%3Aboolean%3Afalse";
    mes.getURI shouldBe new URI("http://example.com");
    mes.getAllHeaders()(0).toString shouldBe "Accept: application/trix;text/boolean"
  }
}
