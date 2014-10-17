package ab.nlp.assignment2

import ab.nlp.Word
import ab.nlp.assignment1.RareWords

/**
 * Created by adam at 17/10/2014 12:24
 */
class CKY(training: Iterable[Tree]) extends (List[Word] => Tree) {
  private val robustify: (Word => Word) = {
    val words = for (t <- training) yield t.words
    new RareWords(words.flatten).toMapping
  }
  private val rules: Iterable[Rule] = (for (t <- training) yield t.mapWords(robustify).rules).flatten
  private val ruleCounts: Map[Rule, Int] = rules.groupBy(r => r).mapValues(_.size).toList.toMap
  private val tagCounts: Map[Tag, Int] = rules.groupBy(_.tag).mapValues(_.size).toList.toMap
  private val ruleSet: Set[Rule] = rules.toSet
  private val tags: Set[Tag] = tagCounts.keySet
  private def q(r: Rule): Double = {
    ruleCounts.getOrElse(r, 0) / tagCounts(r.tag).toDouble
  }
  private val tagToBinaryRules: Map[Tag, Set[BinaryRule]] = {
    val binaryRules = (for (t <- training) yield t.binaryRules).flatten.toSet
    (for (tag <- tags) yield tag -> binaryRules.filter(_.tag == tag)).toMap
  }

  def apply(s: List[Word]): Tree = {
    case class Key(i: Int, j: Int, tag: Tag)
    case class Split(rule: BinaryRule, splitAt: Int) {
      def left(key: Key) = Key(key.i, splitAt, rule.left)
      def right(key: Key) = Key(splitAt + 1, key.j, rule.right)
    }
    case class Value(pi: Double, split: Option[Split])
    type Table = Map[Key, Value]
    val n = s.size
    def calc(width: Int, table: Table): Table = {
      if (width == n) table
      else {
        def findBest(i: Int, j: Int, rules: Set[BinaryRule]): Value = {
          if (rules.isEmpty) Value(0, None)
          else {
            val values = for (rule <- rules; splitAt <- i until j) yield {
              val qRule: Double = q(rule)
              val value0: Value = table(Key(i, splitAt, rule.left))
              val value1: Value = table(Key(splitAt + 1, j, rule.right))
              val pi = qRule * value0.pi * value1.pi
              Value(pi, Some(Split(rule, splitAt)))
            }
            values.maxBy(_.pi)
          }
        }
        val bests = for (i <- 1 to (n - width); (tag, rules) <- tagToBinaryRules) yield {
          val j = i + width
          Key(i, j, tag) -> findBest(i, j, rules)
        }
        calc(width + 1, table ++ bests)
      }
    }
    val table = calc(1, (for (i <- 1 to n; tag <- tags) yield {
      Key(i, i, tag) -> Value(q(UnaryRule(tag, robustify(s(i - 1)))), None)
    }).toMap)
    def toTree(key: Key): Tree = table(key) match {
      case Value(_, Some(split)) => Node(key.tag, toTree(split.left(key)), toTree(split.right(key)))
      case Value(_, None) => Leaf(key.tag, s(key.i - 1))
    }
    toTree(Key(1, n, "SBARQ"))
  }
}
