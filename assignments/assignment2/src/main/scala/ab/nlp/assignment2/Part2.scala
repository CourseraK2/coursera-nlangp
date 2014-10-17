package ab.nlp.assignment2

import java.io.{BufferedWriter, FileWriter}

/**
 * Created by adam at 17/10/2014 15:10
 */
object Part2 extends App {
  val cky = new CKY(DataSets.training)
  val trees = for (s <- DataSets.dev) yield cky(s)
  val out = new BufferedWriter(new FileWriter("results/parse_dev.out"))
  trees.foreach(t => out.write(Tree.toJson(t) + "\n"))
  out.close()
}
