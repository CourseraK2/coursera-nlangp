package ab.nlp.assignment2

import ab.nlp.test.UnitSpec

/**
 * Created by adam at 17/10/2014 10:40
 */
class TreeSpec extends UnitSpec {
  "The json parser" should "parse single leaf correctly" in {
    Tree.fromJson("[\"TAG\", \"word\"]") shouldBe Leaf("TAG", "word")
  }
  it should "parse full tree correctly" in {
    Tree.fromJson( """["TAG1", ["TAG2", "word1"],  ["TAG2", "word2"]]""") shouldBe Node(
      "TAG1",
      Leaf("TAG2", "word1"),
      Leaf("TAG2", "word2")
    )
  }

  trait SimpleTree {
    def leaf(word: String) = Leaf("LEAF", word)
    def node(left: Tree, right: Tree) = Node("NODE", left, right)
    val tree = node(
      node(
        leaf("word1"),
        leaf("word2")
      ),
      leaf("word3")
    )
  }

  "The words method" should "return words in correct order" in new SimpleTree {
    tree.words shouldBe List("word1", "word2", "word3")
  }
  "The tags method" should "return tags in correct order" in new SimpleTree {
    tree.tags shouldBe List("NODE", "NODE", "LEAF", "LEAF", "LEAF")
  }
  "The binaryRules method" should "return the rules" in new SimpleTree {
    tree.binaryRules should contain only(BinaryRule("NODE", "NODE", "LEAF"), BinaryRule("NODE", "LEAF", "LEAF"))
  }
  "The unaryRiles method" should "return the correct rules" in new SimpleTree {
    tree.binaryRules should contain only(UnaryRule("LEAF", "word1"), UnaryRule("LEAF", "word2"), UnaryRule("LEAF", "word2"))
  }

  "The mapWords method" should "map words according to supplied function" in new SimpleTree {
    tree.mapWords(f => "mapped") shouldBe node(
      node(
        leaf("mapped"),
        leaf("mapped")
      ),
      leaf("mapped")
    )
  }
}
