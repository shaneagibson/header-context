package uk.co.epsilontechnologies.headercontext

import java.util.concurrent.TimeUnit

import akka.dispatch._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

class ContextPropagatingDispatcherConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends MessageDispatcherConfigurator(config, prerequisites) {

  private val instance = new ContextPropagatingDispatcher(
    this,
    config.getString("id"),
    config.getInt("throughput"),
    FiniteDuration(config.getDuration("throughput-deadline-time", TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
    configureExecutor(),
    FiniteDuration(config.getDuration("shutdown-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS))

  override def dispatcher(): MessageDispatcher = instance
  
}

class ContextPropagatingDispatcher(
          _configurator: MessageDispatcherConfigurator,
          id: String,
          throughput: Int,
          throughputDeadlineTime: Duration,
          executorServiceFactoryProvider: ExecutorServiceFactoryProvider,
          shutdownTimeout: FiniteDuration)
  extends Dispatcher(
          _configurator,
          id,
          throughput,
          throughputDeadlineTime,
          executorServiceFactoryProvider,
          shutdownTimeout) {

  self =>

  override def prepare(): ExecutionContext = new ExecutionContext {

    val context = Context.getMap

    def execute(r: Runnable) = self.execute(new Runnable {
      def run() = {
        val oldContext = Context.getMap
        setContextMap(context)
        try {
          r.run()
        } finally {
          setContextMap(oldContext)
        }
      }
    })

    def reportFailure(t: Throwable) = self.reportFailure(t)

  }

  private[this] def setContextMap(context: Map[String,Object]) {
    if (context == null) {
      Context.clear()
    } else {
      Context.setMap(context)
    }
  }

}