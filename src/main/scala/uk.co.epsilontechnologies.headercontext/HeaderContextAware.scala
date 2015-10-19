package uk.co.epsilontechnologies.headercontext

import play.api.mvc.Headers

trait HeaderContextAware {

  def captureHeaders(headers: Headers) = {
    Context.put("headers", headers)
  }

  def retrieveHeaders(): Map[String,String] = {
    Context.get("headers").map(_.asInstanceOf[Headers]) match {
      case Some(headers) => headers.toSimpleMap
      case None => Map()
    }
  }

}