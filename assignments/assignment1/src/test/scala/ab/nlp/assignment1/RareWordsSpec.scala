package ab.nlp.assignment1

import ab.nlp.test.UnitSpec

/**
 * Created by adam at 16/10/2014 14:49
 */
class RareWordsSpec extends UnitSpec {
  val words = Iterable("common", "common", "rare")
  val rare = new RareWords(words, 2)

  "Rare words method" should "return false for words that hit threshold" in {
    rare("common") shouldBe false
  }
  it should "return true for words that do not hit threshold" in {
    rare("rare") shouldBe false
  }
  it should "return true for unknown words" in {
    rare("unknown") shouldBe false
  }
}
