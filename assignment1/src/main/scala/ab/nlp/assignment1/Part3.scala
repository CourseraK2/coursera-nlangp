package ab.nlp.assignment1

import java.io.{FileWriter, BufferedWriter}

/**
 * Created by adam at 16/10/2014 14:39
 */
object Part3 extends App {
  val rare = new RareWords(DataSets.training.flatten.map(_._1))
  val mapping = (w: Word) => if (rare(w)) {
    if (w.matches(".*[0-9].*")) "<NUMERIC>"
    else if (w == w.toUpperCase) "<UPPERCASE>"
    else if (w.matches(".*[A-Z]")) "<LASTCAP>"
    else "<RARE>"
  } else w
  val hmm = new HMM(DataSets.training).mapWords(mapping)
  val viterbi = new Viterbi(hmm)
  val devWordTags: List[List[WordTag]] = for (s <- DataSets.dev) yield viterbi(s)
  val out = new BufferedWriter(new FileWriter("results/gene_dev.p3.out"))
  devWordTags.foreach(s => {
    s.foreach {
      case (w, t) => out.write(s"$w $t\n")
    }
    out.write("\n")
  })
  out.close()
}
