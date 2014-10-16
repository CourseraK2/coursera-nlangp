package ab.nlp.assignment1

import java.io.{FileWriter, BufferedWriter}

/**
 * Created by adam at 15/10/2014 09:13
 */
object Part1 extends App {
  val hmm = new HMM(DataSets.training).robust
  def mostLikelyTag(w: Word): Tag = {
    hmm.S.reduce((x, y) => {
      if (hmm.e(w, x) > hmm.e(w, y)) x else y
    })
  }
  val devWordTags = for (s <- DataSets.dev) yield {
    for (w <- s) yield w -> mostLikelyTag(w)
  }
  val out = new BufferedWriter(new FileWriter("results/gene_dev.p1.out"))
  devWordTags.foreach(s => {
    s.foreach {
      case (w, t) => out.write(s"$w $t\n")
    }
    out.write("\n")
  })
  out.close()
}
