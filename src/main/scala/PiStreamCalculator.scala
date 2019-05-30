import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging
import org.apfloat.{Apfloat, ApfloatMath, Apint, ApintMath}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class PiStreamCalculator(precision: Long, tasks: Int, quiet: Boolean) extends LazyLogging {
  private val threadPool = Executors.newFixedThreadPool(tasks)
  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(threadPool)

  def calculate(): Apfloat = {
    new Apfloat(1) divide oneOverPi
  }

  private def sqrt2(): Apfloat = ApfloatMath.sqrt(new Apfloat(2).precision(precision))


  private def oneOverPi: Apfloat = {
    val const = sqrt2().multiply(new Apfloat(2)).divide(new Apfloat(9801))

    val futurePartialSums = for (t <- 0 until tasks) yield getPartialSums(t)
    val sum = Await.result(Future.sequence(futurePartialSums), Duration.Inf).reduce((cur, acc) => cur.add(acc))

    const.multiply(sum)
  }

  private def getPartialSums(task: Int): Future[Apfloat] = {
    Future {
      if (!quiet) logger.info("Thread {} started", task + 1)
      val timingResult = Timer.time {
        addSums(task)
      }
      if (!quiet) logger.info("Thread {} finished. Execution time was {}ms", task + 1, timingResult.time)

      timingResult.result
    }
  }

  def addSums(task: Int): Apfloat = {
    var sum: Apfloat = new Apfloat(0)

    var k = task
    var newNum = new Apfloat(0)
    do {
      newNum = getPartialSum(k)
      sum = sum.add(newNum)
      k += tasks
    } while(newNum.scale > -precision)

    sum
  }

  private def getPartialSum(k: Int): Apfloat = {
    val factorial4k: Apint = factorialStream(4 * k)
    val factkToThe4th: Apint = ApintMath.pow(factorialStream(k), 4)
    val denom: Apint = denominators(k)
    val num = numerators(k)

    factorial4k.multiply(num).divide(factkToThe4th.multiply(denom).real().precision(precision))
  }

  private val factorialStream: Stream[Apint] = new Apint(1) #:: (factorialStream zip Stream.from(1)).map { case (x, y) => x.multiply(new Apint(y)) }
  private val denominators: Stream[Apint] = new Apint(1) #:: (denominators zip Stream.continually(new Apint(24591257856l))).map { case (x, y) => x.multiply(y) }
  private val numerators: Stream[Apint] = new Apint(1103) #:: (numerators zip Stream.continually(new Apint(26390))).map { case (x, y) => x.add(y) }
}