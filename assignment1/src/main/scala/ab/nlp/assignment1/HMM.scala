package ab.nlp.assignment1

/**
 * Created by adam at 16/10/2014 10:34
 */
class HMM(training: List[List[WordTag]]) {
  private def toCountMap[A](as: List[A]): Map[A, Int] = as.groupBy(a => a).mapValues(_.size)

  /** @return set of all possible words */
  val V: Set[Word] = training.flatten.map(_._1).toSet

  /** @return set of all possible tags */
  val S: Set[Tag] = training.flatten.map(_._2).toSet

  private val tagNgramCounts: Map[NGram, Int] = {
    def ngramCounts(n: Int)(sentences: List[List[Tag]]): Map[NGram, Int] = {
      toCountMap(sentences.map(NGramExtractor(n)).flatten)
    }
    val tags = for (s <- training) yield {
      for ((w, t) <- s) yield t
    }
    ngramCounts(3)(tags) ++ ngramCounts(2)(tags) ++ ngramCounts(1)(tags) ++ Map(
      //Special cases required for q function
      NGram(Tag.*, Tag.*) -> training.size,
      NGram(Tag.*) -> training.size,
      NGram() -> tags.flatten.size
    )
  }

  private val wordTagCounts: Map[WordTag, Int] = toCountMap(training.flatten)

  private def checkTag(t: Tag, valid: Set[Tag]) = require(valid.contains(t), s"$t not present in $valid")

  /** @return emission probability for given word/tag */
  def e(w: Word, t: Tag): Double = {
    checkTag(t, S)
    wordTagCounts.getOrElse((w, t), 0) / tagNgramCounts.getOrElse(List(t), 0).toDouble
  }

  /** @return transition probability for given ngram */
  def q(ngram: NGram): Double = {
    ngram.foreach(checkTag(_, S + Tag.* + Tag.STOP))
    val num = tagNgramCounts.getOrElse(ngram, 0).toDouble
    val den = tagNgramCounts.getOrElse(ngram.init, 0).toDouble
    num / den
  }

  /** @return a HMM where rare words are replaced with the _RARE_ symbol */
  def robust = {
    def totals[A](counts: List[(A, Int)]): Map[A, Int] = {
      counts.groupBy(_._1).mapValues {
        _.map(_._2).sum
      }.toList.toMap //Convert to list & back to get fresh impl as map produced by groupBy seems to be v slow
    }
    val wordCounts: Map[Word, Int] = totals(for (((w, t), c) <- wordTagCounts.toList) yield w -> c)
    val isCommon: (Word => Boolean) = (for ((w, c) <- wordCounts if c >= 5) yield w).toSet
    def robustify(w: Word): Word = if (isCommon(w)) w else "_RARE_"
    val robustified: List[List[WordTag]] = for (s <- training) yield {
      for ((w, t) <- s) yield (robustify(w), t)
    }
    new HMM(robustified) {
      override def e(w: Word, t: Tag) = super.e(robustify(w), t)
    }
  }
}
