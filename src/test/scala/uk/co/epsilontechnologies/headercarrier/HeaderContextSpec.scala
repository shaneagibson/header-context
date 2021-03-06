package uk.co.epsilontechnologies.headercarrier

import org.scalatest.{Matchers, WordSpecLike}
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.test.{TestServer, FakeApplication}
import play.api.test.Helpers._
import play.api.mvc.Results._
import uk.co.epsilontechnologies.headercontext.HeaderContextAware

import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class HeaderContextSpec extends WordSpecLike with Matchers with HeaderContextAware {

  "HeaderContext" should {

    "capture and fetch header values in the same thread" in running(server) {
      await(WS.url("http://localhost:9000/same-thread").withHeaders(("test", "same-thread")).get()).status shouldBe 200
    }

    "capture and fetch header values in different threads" in running(server) {
      await(WS.url("http://localhost:9000/different-thread").withHeaders(("test", "different-thread")).get()).status shouldBe 200
    }

    "capture and fetch header values when multiple requests are issued in parallel" in running(server) {
      val futures = (1 to 1000).map(i => {
        WS.url(s"http://localhost:9000/parallel?id=$i").withHeaders(("test", s"parallel-$i")).get()
      })
      await(Future.sequence(futures)).foreach(result => result.status shouldBe 200)
    }

  }

  private val server = TestServer(9000, FakeApplication(
    additionalConfiguration = Map("play.akka.actor.default-dispatcher.type" -> "uk.co.epsilontechnologies.headercontext.ContextPropagatingDispatcherConfigurator"),
    withRoutes = {
      case ("GET", "/same-thread") => sameThread()
      case ("GET", "/different-thread") => differentThread()
      case ("GET", "/parallel") => parallel()
    }
  ))

  private def sameThread() =  Action.async {
    implicit request => {
      captureHeaders(request.headers)
      retrieveHeaders().getOrElse("test", "") match {
        case "same-thread" => Future(Ok(""))
        case _ => Future(BadRequest(s"test header should be 'same-thread'"))
      }
    }
  }

  private def differentThread() = Action.async {
    implicit request => {
      captureHeaders(request.headers)
      Future.successful {
        Thread.sleep(1000)
        retrieveHeaders().getOrElse("test", "") match {
          case "different-thread" => Ok("")
          case _ => BadRequest(s"test header should be 'different-thread'")
        }
      }
    }
  }

  private def parallel() = Action.async {
    implicit request => {
      val id = request.getQueryString("id").getOrElse("")
      captureHeaders(request.headers)
      Future.successful {
        retrieveHeaders().getOrElse("test", "").equals(s"parallel-$id") match {
          case true => Ok("")
          case false => BadRequest(s"test header should be 'parallel-$id'")
        }
      }
    }
  }

}
