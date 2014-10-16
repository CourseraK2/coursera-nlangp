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

  /** @return a derived HMM where words are mapped by the provided function */
  def mapWords(f: Word => Word): HMM = {
    val mapped: List[List[WordTag]] = for (s <- training) yield {
      for ((w, t) <- s) yield (f(w), t)
    }
    new HMM(mapped) {
      override def e(w: Word, t: Tag) = super.e(f(w), t)
    }
  }

  /** @return a derived HMM where rare words are replaced with the _RARE_ symbol */
  def robust = mapWords(new RareWords(training.flatten.map(_._1)).toMapping)
}