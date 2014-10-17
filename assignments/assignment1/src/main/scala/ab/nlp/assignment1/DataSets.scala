package ab.nlp.assignment1

import ab.nlp.Word

import scala.annotation.tailrec
import scala.io.Source

/**
 * Created by adam at 16/10/2014 08:35
 */
object DataSets {
  lazy val training: List[List[WordTag]] = {
    for (s <- groupSentences(load("/gene.train"))) yield {
      for (l <- s) yield {
        val w :: t :: Nil = l.split(" ").toList
        (w, t)
      }

    }
  }
  lazy val dev: List[List[Word]] = groupSentences(load("/gene.dev"))

  private def groupSentences(lines: List[String]): List[List[String]] = {
    @tailrec
    def doGrouping(groups: List[List[String]], lines: List[String]): List[List[String]] = {
      if (lines.isEmpty) groups
      else {
        val g = lines.takeWhile(!_.isEmpty)
        doGrouping(g :: groups, lines.drop
          (g.size + 1))
      }
    }
    doGrouping(Nil, lines).reverse
  }

  private def load(name: String): List[String] = {
    Source.fromInputStream(getClass.getResourceAsStream(name)).getLines().toList
  }
}
