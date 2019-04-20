import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, HelpFormatter, Option, Options}

import scala.util.Try

object CliParser {
  private val parser: CommandLineParser = new DefaultParser
  private val formatter = new HelpFormatter

  val precisionOption: Option = Option.builder("p")
    .longOpt("precision")
    .hasArg(true)
    .argName("decimal-places")
    .required(false)
    .desc("Number of decimal places precision")
    .build()

  val tasksOption: Option = Option.builder("t")
    .longOpt("tasks")
    .hasArg(true)
    .argName("num-tasks")
    .required(false)
    .desc("Number of tasks to be used")
    .build()

  val outputOption: Option = Option.builder("o")
    .longOpt("output")
    .hasArg(true)
    .argName("output-file")
    .required(false)
    .desc("Output file for the program result")
    .build()

  val quietOption: Option = Option.builder("q")
    .longOpt("quiet")
    .required(false)
    .desc("Quiet mode (Do not show logs)")
    .build()

  def parse(args: Array[String]): Try[CommandLine] = {
    Try(parser.parse(options, args))
  }

  def printHelp(): Unit = {
    formatter.printHelp("pi", options, true)
  }

  private val options: Options = {
    val options: Options = new Options()

    options.addOption(precisionOption)
    options.addOption(tasksOption)
    options.addOption(outputOption)
    options.addOption(quietOption)

    options
  }
}
