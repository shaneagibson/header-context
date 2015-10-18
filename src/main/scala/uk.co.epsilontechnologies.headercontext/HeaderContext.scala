package uk.co.epsilontechnologies.headercontext

import org.slf4j.MDC
import play.api.mvc.Headers

trait HeaderContext {

  def captureHeaders(headers: Headers) = {
    Context.put("headers", headers)
    headers.keys.foreach(headerKey => {
      MDC.put(headerKey, headers.get(headerKey).getOrElse(""))
    })
  }

  def retrieveHeaders(): Map[String,String] = {
    Context.get("headers").map(_.asInstanceOf[Headers]) match {
      case Some(headers) => headers.toSimpleMap
      case None => Map()
    }
  }

}