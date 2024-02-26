package proj.scalaadvlrn

import com.typesafe.scalalogging.Logger

import java.time.Duration
import java.util.Random
import java.util.concurrent.Executors
import scala.collection.mutable

object ThreadComms extends App {

  // Producer/Consumer problem
  // Forcing threads to perform operations in a guaranteed order - even though the threads are running concurrently

  // Create a Buffer
  // Producer has to pause producing values to the buffer when the buffer is full
  // Consumer has to consume values when available

  class Buffer(bufferSize: Int = 5) {
    var storage: mutable.Queue[Int] = new mutable.Queue[Int](bufferSize)
    def gotSpace(): Boolean = storage.size < bufferSize
    def isBufferEmpty: Boolean = storage.isEmpty

    private val randomizer = new Random()

    def initializeBuffer(): Unit = while (gotSpace()) {
      storage.enqueue(randomizer.nextInt(1500))
    }

    override def toString: String = storage.synchronized {
      if (storage.nonEmpty) storage.foldLeft("")(_ + "," + _).substring(1) else ""
    }
  }

  class ProducerConsumer(val buffer: Buffer, val name: String)
  trait Producer {
    def set(v: Int): Unit
  }
  trait Consumer {
    def get(): Unit
  }

  class Prod(override val buffer: Buffer, override val name: String = "prod") extends
    ProducerConsumer(buffer, name) with Producer
  {
    private val logger: Logger = Logger(getClass.getName)

    def set(value: Int): Unit = buffer.synchronized {
      while (!buffer.gotSpace()) { // TODO: NOTE - When this while() was if(), the buffer was being added to even when it was > 5
        logger.info(s"Producer $name waiting as Buffer is full")
        buffer.wait()
      }
      logger.info(s"Producer $name producing value $value")
      buffer.storage += value
      buffer.notify()
    }
  }

  class Cons(override val buffer: Buffer, override val name: String = "cons") extends
    ProducerConsumer(buffer, name) with Consumer
  {
    private val logger: Logger = Logger(getClass.getName)
    def get(): Unit = buffer.synchronized {
      while (buffer.isBufferEmpty) { // TODO: NOTE - When this while() was if(), the buffer was being added to even when it was > 5
        logger.info(s"Consumer $name waiting as Buffer is empty")
        buffer.wait()
      }
      val readVal = buffer.storage.dequeue()
      logger.info(s"Consumer $name consuming value in buffer: $readVal")
      buffer.notify()
    }
  }

  val threadPool = Executors.newFixedThreadPool(10)

  // Buffer init
  val buffer = new Buffer()
  val randomGen = new java.util.Random()

  buffer.initializeBuffer()
  println(s"State of the buffer at the start is: ${buffer.toString}")
  assert(
    buffer.toString match {
      case x: String => true
      case _ => false
    }
  )
  Thread.sleep(5000)

  // Prod/Cons init
  val listProducers: List[Producer] = (1 to 2).map(x => new Prod(buffer, s"Prod$x")).toList
  val listConsumers: List[Consumer] = (1 to 3).map(y => new Cons(buffer, s"Cons$y")).toList

  // Run each producer set() in the threadpool
  listProducers.foreach(p => {
      threadPool.execute(() => {
        while(true) {
          p.set(randomGen.nextInt(50))
          Thread.sleep(randomGen.nextInt(500))
        }
      })
    })

  // Run get consumer get() in the threadpool
  listConsumers.foreach(c => {
    threadPool.execute(() => {
      while(true) {
        c.get()
        Thread.sleep(randomGen.nextInt(500))
      }
    })
  })

  // Print state of the buffer occasionally
  threadPool.execute(() => {
    // Print state of the buffer
    while(true) {
      println(s"Current state of the buffer is: ${buffer.toString}")
      Thread.sleep(1000)
    }
  })

//  threadPool.execute(() => {
//    while (true) {
//      consumer.get()
//      Thread.sleep(randomGen.nextInt(500))
//    }
//  })
//
//  threadPool.execute(() => {
//    while (true) {
//      producer.set(randomGen.nextInt(50))
//      Thread.sleep(randomGen.nextInt(500))
//    }
//  })

  threadPool.shutdown()

}
