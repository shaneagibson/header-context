package uk.co.epsilontechnologies.headercarrier

import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpecLike}
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.test.{TestServer, FakeApplication}
import play.api.test.Helpers._
import play.api.mvc.Results._
import uk.co.epsilontechnologies.headercontext.{Context, HeaderContext}

import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class HeaderContextSpec extends WordSpecLike with Matchers with HeaderContext with BeforeAndAfterEach {

  "HeaderContext" should {

    def sameThread() =  Action.async {
      implicit request => {
        captureHeaders(request.headers)
        retrieveHeaders().getOrElse("test", "") match {
          case "same-thread" => Future(Ok(""))
          case _ => Future(BadRequest(s"test header should be 'same-thread'"))
        }
      }
    }

    def differentThread() = Action.async {
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

    val application = FakeApplication(
      additionalConfiguration = Map("play.akka.actor.default-dispatcher.type" -> "uk.co.epsilontechnologies.headercontext.ContextPropagatingDispatcherConfigurator"),
      withRoutes = {
        case ("GET", "/same-thread") => sameThread()
        case ("GET", "/different-thread") => differentThread()
      }
    )

    val server = TestServer(9000, application)

    "capture and fetch header values in the same thread" in running(server) {
      await(WS.url("http://localhost:9000/same-thread").withHeaders(("test", "same-thread")).get()).status shouldBe 200
    }

    "capture and fetch header values in a different thread" in running(server) {
      await(WS.url("http://localhost:9000/different-thread").withHeaders(("test", "different-thread")).get()).status shouldBe 200
    }

    // TODO - add test for simultaneous requests

  }

  override protected def afterEach() = {
    Context.clear()
  }

}
