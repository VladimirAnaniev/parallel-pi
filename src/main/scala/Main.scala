import java.io.{File, PrintWriter}

//import com.sun.java.util.jar.pack.Package.File
import org.apache.commons.cli.CommandLine
import org.apfloat.Apfloat

//import scala.sys.process.processInternal.File
import scala.util.{Failure, Success}


object Main {
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

    println(s"$tasks $precision $quiet")

    val t1 = System.nanoTime

    val writer = new PrintWriter(new File(output))
    writer.write(PiCalculator.getPi(precision, tasks).toString())
    writer.close()

    val duration = (System.nanoTime - t1) / 1e9d
    println(duration + " seconds")
  }
}
