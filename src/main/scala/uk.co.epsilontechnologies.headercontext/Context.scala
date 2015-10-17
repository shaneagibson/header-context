package uk.co.epsilontechnologies.headercontext

object Context {

  private[this] val lock = new Object()

  private[this] val threadLocal: ThreadLocal[Map[String,Object]] = new ThreadLocal()

  def getMap: Map[String,Object] = {
    lock.synchronized {
      if (this.threadLocal.get() == null) {
        Map()
      } else {
        this.threadLocal.get()
      }
    }
  }

  def setMap(context: Map[String,Object]): Unit = {
    this.threadLocal.set(context)
  }

  def clear(): Unit = {
    this.threadLocal.remove()
  }

  def get(key: String): Option[Object] = {
    getMap.get(key)
  }

  def put(key: String, value: Object) = {
    lock.synchronized {
      setMap(getMap + (key -> value))
    }
  }

  def remove(key: String) = {
    lock.synchronized {
      setMap(getMap - key)
    }
  }

}