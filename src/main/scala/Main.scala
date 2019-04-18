import scala.util.{Failure, Success, Try}


object Main {
  def main(args: Array[String]) {
    val commandLine = CliParser.parse(args)

    commandLine match {
      case Success(line) => println("Nice")
      case Failure(_) => CliParser.printHelp()
    }
  }
}

