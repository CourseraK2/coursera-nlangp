package ab.nlp.assignment2

import ab.nlp.Word

import scala.io.Source

/**
 * Created by adam at 17/10/2014 10:34
 */
object DataSets {
  lazy val training: List[Tree] = for (s <- load("/parse_train.dat")) yield Tree.fromJson(s)
  lazy val trainingVert: List[Tree] = for (s <- load("/parse_train_vert.dat")) yield Tree.fromJson(s)

  lazy val dev: List[List[Word]] = for (s <- load("/parse_dev.dat")) yield s.split(" ").toList

  private def load(name: String): List[String] = {
    Source.fromInputStream(getClass.getResourceAsStream(name)).getLines().toList
  }
}
