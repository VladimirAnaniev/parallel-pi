import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging
import org.apfloat.{Apfloat, ApfloatMath, Apint, ApintMath}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class PiCalculator(precision: Long, tasks: Int, quiet: Boolean) extends LazyLogging {
  private val threadPool = Executors.newFixedThreadPool(tasks)
  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(threadPool)

  private def factorial(k: Int): Apint = {
    ApintMath.factorial(k)
  }

  private def denominator(k: Int): Apint = ApintMath.pow(new Apint(24591257856l), k)

  private def numerator(k: Int): Apint = new Apint(1103).add(new Apint(26390).multiply(new Apint(k)))

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
    val factorial4k: Apint = factorial(4 * k)
    val factkToThe4th: Apint = ApintMath.pow(factorial(k), 4)
    val denom: Apint = denominator(k)
    val num = numerator(k)

    factorial4k.multiply(num).divide(factkToThe4th.multiply(denom).real().precision(precision))
  }
}
