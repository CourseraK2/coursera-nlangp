package ab.nlp.assignment1

/**
 * Created by adam at 16/10/2014 13:04
 */
object NGramExtractor {
  def apply(n: Int)(s: List[Tag]): List[NGram] = {
    def unigrams = (s :+ Tag.STOP).map(NGram(_))
    def ngrams(prefix: NGram, s: List[Tag]): List[NGram] = {
      if (s.isEmpty) (prefix :+ Tag.STOP) :: Nil
      else {
        (prefix :+ s.head) :: ngrams(
          prefix.tail :+ s.head,
          s.tail
        )
      }
    }
    require(n > 0)
    if (n == 1) unigrams else ngrams(NGram.fill(n - 1)(Tag.*), s)
  }
}
