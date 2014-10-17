package ab.nlp.assignment2

/**
 * Created by adam at 17/10/2014 11:58
 */
object Part1 extends App {
  def printCounts(trees: List[Tree]) = {
    val tags = for (t <- trees) yield t.tags
    tags.flatten.groupBy(t => t).mapValues(_.size).foreach {
      case (t, n) => println(s"$n NONTERMINAL $t")
    }
    val rules = for (t <- trees) yield t.rules
    rules.flatten.groupBy(r => r).mapValues(_.size).foreach {
      case (BinaryRule(t, l, r), n) => println(s"$n BINARYRULE $t $l $r")
      case (UnaryRule(t, w), n) => println(s"$n UNARYRULE $t $w")
    }
  }
  printCounts(DataSets.training)
}
