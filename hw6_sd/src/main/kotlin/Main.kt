fun main(args: Array<String>) {
    val (str, answer) = Calculator.parseAndEvaluate(args[0])
    println(str)
    println(answer)
}
