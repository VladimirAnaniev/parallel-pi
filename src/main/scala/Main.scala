import java.io.{File, PrintWriter}

import com.typesafe.scalalogging.LazyLogging

import org.apache.commons.cli.CommandLine

import scala.util.{Failure, Success}


object Main extends LazyLogging {
  private val DEFAULT_TASKS = "1"
  private val DEFAULT_PRECISION = "100"
  private val DEFAULT_OUTPUT_FILE = "output.txt"

  def main(args: Array[String]): Unit = {
    val commandLine = CliParser.parse(args)

    commandLine match {
      case Success(line) => run(line)
      case Failure(e) => println(e.getMessage); CliParser.printHelp()
    }
  }

  def run(parameters: CommandLine): Unit = {
    val tasks: Int = parameters.getOptionValue(CliParser.tasksOption.getOpt, DEFAULT_TASKS).toInt
    val precision: Int = parameters.getOptionValue(CliParser.precisionOption.getOpt, DEFAULT_PRECISION).toInt
    val output: String = parameters.getOptionValue(CliParser.outputOption.getOpt, DEFAULT_OUTPUT_FILE)
    val quiet: Boolean = parameters.hasOption(CliParser.quietOption.getOpt)


    logger.info("Starting pi calculation with precision {}. Number of tasks: {} ", precision, tasks)

    val timingResult = Timer.time {
      val writer = new PrintWriter(new File(output))
      writer.write(PiCalculator(precision, tasks).calculate().toString())
      writer.close()
    }

    logger.info("Total execution time {}ms", timingResult.time)
  }
}
