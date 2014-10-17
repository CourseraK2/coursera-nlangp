package ab.nlp.assignment1

import ab.nlp.Word

/**
 * Created by adam at 16/10/2014 14:43
 */
class RareWords(words: Iterable[Word], threshold: Int = 5) extends (Word => Boolean) {
  private val isCommon: (Word => Boolean) = {
    val wordCounts: Map[Word, Int] = words.groupBy(w => w).mapValues(_.size)
    (for ((w, c) <- wordCounts if c >= threshold) yield w).toSet
  }

  def apply(w: Word): Boolean = !isCommon(w)

  def toMapping: Word => Word = (w: Word) => if (isCommon(w)) w else "_RARE_"
}
