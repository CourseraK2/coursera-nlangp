package ab.nlp.assignment2

import java.io.{BufferedWriter, FileWriter}

/**
 * Created by adam at 17/10/2014 15:10
 */
object Part3 extends App {
  val cky = new CKY(DataSets.trainingVert)
  val trees = for (s <- DataSets.dev) yield cky(s)
  val out = new BufferedWriter(new FileWriter("results/parse_dev_vert.out"))
  trees.foreach(t => out.write(Tree.toJson(t) + "\n"))
  out.close()
}
