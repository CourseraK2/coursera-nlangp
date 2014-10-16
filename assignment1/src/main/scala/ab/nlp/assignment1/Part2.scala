package ab.nlp.assignment1

import java.io.{BufferedWriter, FileWriter}

/**
 * Created by adam at 16/10/2014 08:33
 */
object Part2 extends App {
  val hmm = new HMM(DataSets.training).robust
  val viterbi = new Viterbi(hmm)
  val devWordTags: List[List[WordTag]] = for (s <- DataSets.dev) yield {
    s zip viterbi(s)
  }
  val out = new BufferedWriter(new FileWriter("results/gene_dev.p2.out"))
  devWordTags.foreach(s => {
    s.foreach {
      case (w, t) => out.write(s"$w $t\n")
    }
    out.write("\n")
  })
  out.close()
}
