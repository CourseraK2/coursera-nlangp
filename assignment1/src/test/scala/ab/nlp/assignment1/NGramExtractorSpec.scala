package ab.nlp.assignment1

import ab.nlp.test.UnitSpec

/**
 * Created by adam at 16/10/2014 13:05
 */
class NGramExtractorSpec extends UnitSpec {
  "The n-gram extractor" should "extract correct trigrams" in {
    NGramExtractor(3)(List("A", "B", "C")) shouldBe List(
      NGram(Tag.*, Tag.*, "A"),
      NGram(Tag.*, "A", "B"),
      NGram("A", "B", "C"),
      NGram("B", "C", Tag.STOP)
    )
  }
  it should "extract correct bigrams" in {
    NGramExtractor(2)(List("A", "B")) shouldBe List(
      NGram(Tag.*, "A"),
      NGram("A", "B"),
      NGram("B", Tag.STOP)
    )
  }
  it should "extract correct unigrams" in {
    NGramExtractor(1)(List("A", "B")) shouldBe List(
      NGram("A"),
      NGram("B"),
      NGram(Tag.STOP)
    )
  }
}
