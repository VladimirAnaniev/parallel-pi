import org.apfloat.{Apfloat, ApfloatMath, Apint, ApintMath}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import scala.concurrent.ExecutionContext.Implicits.global

object PiCalculator {
  def getPi(precision: Long, tasks: Int = 1): Apfloat = new Apfloat(1) divide getOneOverPi(precision, tasks)

  private val factorialStream: Stream[Apint] = new Apint(1) #:: (factorialStream zip Stream.from(1)).map { case (x, y) => x.multiply(new Apint(y)) }
  private val denominators: Stream[Apint] = new Apint(1) #:: (denominators zip Stream.continually(new Apint(24591257856l))).map { case (x, y) => x.multiply(y) }
  private val numerators: Stream[Apint] = new Apint(1103) #:: (numerators zip Stream.continually(new Apint(26390))).map { case (x, y) => x.add(y) }

  private def sqrt2(precision: Long): Apfloat = ApfloatMath.sqrt(new Apfloat(2, precision))

  private def getPartialSum(k: Int, precision: Long): Apfloat = {
    val factorial4k: Apint = factorialStream(4 * k)
    val factkToThe4th: Apint = ApintMath.pow(factorialStream(k), 4)
    val denominator: Apint = denominators(k)
    val numerator = numerators(k)

    factorial4k.multiply(numerator).divide(factkToThe4th.multiply(denominator).real().precision(precision))
  }


  private def getOneOverPi(precision: Long, tasks: Int): Apfloat = {
    val const = sqrt2(precision).multiply(new Apfloat(2)).divide(new Apfloat(9801))

    val futurePartialSums = for (t <- 0 until tasks) yield getFuturePartialSums(t, tasks, precision)

    val h = System.nanoTime
    val sum = Await.result(Future.sequence(futurePartialSums), Duration.Inf).reduce((cur, acc) => cur.add(acc))
    println((System.nanoTime - h) / 1e9d)

    const.multiply(sum)
  }

  private def getFuturePartialSums(task: Int, tasks: Int, precision: Long): Future[Apfloat] = {
    // TODO: refactor this :)
    Future {
      println(s"Thread $task started")
      var sum = new Apfloat(0, precision)

      def addSums(): Unit = {
        for (k <- task to(precision.toInt, tasks)) {
          val newSum = getPartialSum(k, precision)
          sum = sum.add(newSum)
          if (newSum.scale < -precision) return
        }
      }

      addSums()

      println(s"Thread $task finished")
      sum
    }
  }
}
