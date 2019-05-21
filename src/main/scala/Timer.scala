object Timer {
  def time[T](block: => T): TimingResult[T] = {
    val startTime = System.currentTimeMillis()
    val result = block
    val endTime = System.currentTimeMillis()

    val delta = endTime - startTime

    TimingResult(result, delta)
  }
}

case class TimingResult[T](result: T, time: Long)
