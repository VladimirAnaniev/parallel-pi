import java.text.ParseException

import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, HelpFormatter, Option, Options}

import scala.util.Try

object CliParser {
  def parse(args: Array[String]): Try[CommandLine] = {
    val parser: CommandLineParser = new DefaultParser
    Try(parser.parse(options, args))
  }

  def printHelp(): Unit = {
    val formatter = new HelpFormatter
    formatter.printHelp("ant", options, true)
  }

  private val options: Options = {
    val options: Options = new Options()

    val precisionOption: Option = Option.builder("p")
      .hasArg(true)
      .argName("decimal-places")
      .required(true)
      .desc("Number of decimal places precision")
      .build()

    val threadsOption: Option = Option.builder("t")
      .longOpt("tasks")
      .hasArg(true)
      .argName("num-tasks")
      .required(true)
      .desc("Number of tasks to be used")
      .build()

    val quietOption: Option = Option.builder("q")
      .longOpt("quiet")
      .required(false)
      .desc("Quiet mode (Do not show logs)")
      .build()

    options.addOption(precisionOption)
    options.addOption(threadsOption)
    options.addOption(quietOption)

    options
  }
}
