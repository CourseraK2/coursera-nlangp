package ab.nlp.assignment1

import ab.nlp.test.UnitSpec

/**
 * Created by adam at 16/10/2014 12:51
 */
class HMMSpec extends UnitSpec {
  trait Fixture {
    val data: List[List[WordTag]]
    lazy val hmm = new HMM(data)
  }
  trait SimpleData extends Fixture {
    val data = List(List(("word1", "TAG1"), ("word2", "TAG2")))
  }

  "An HMM" should "expose all words in V" in new SimpleData {
    hmm.V shouldBe Set("word1", "word2")
  }
  it should "expose all tags in S" in new SimpleData {
    hmm.S shouldBe Set("TAG1", "TAG2")
  }
  "The q method" should "return 1.0 for only possible transition" in new SimpleData {
    hmm.q(NGram("TAG1", "TAG2")) shouldBe 1.0
  }
  it should "return 1.0 for only bigram present involving STOP" in new SimpleData {
    hmm.q(NGram("TAG2", Tag.STOP)) shouldBe 1.0
  }
  it should "return 1.0 for only bigram present involving *" in new SimpleData {
    hmm.q(NGram(Tag.*, "TAG1")) shouldBe 1.0
  }
  it should "return 1.0 for only trigram present involving * *" in new SimpleData {
    hmm.q(NGram(Tag.*, Tag.*, "TAG1")) shouldBe 1.0
  }
  it should "return 0.0 for unknown sequence" in new SimpleData {
    hmm.q(NGram("TAG2", "TAG1")) shouldBe 0.0
  }
  it should "throw an exception for unknown tag" in new SimpleData {
    intercept[IllegalArgumentException] {
      hmm.q(NGram("UNKNOWN"))
    }
  }
}
