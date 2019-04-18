import scala.annotation.tailrec

object PiCalculator {
  @tailrec
  def factorial(n: BigDecimal, acc: BigDecimal = 1): BigDecimal = {
    assert(n >= 0)

    if (n == 1 || n == 0) acc
    else factorial(n - 1, acc * n)
  }


}


