package ab.nlp.assignment2

import ab.nlp.Word
import spray.json._

/**
 * Created by adam at 17/10/2014 10:36
 */
trait Rule {
  def tag: Tag
}
case class UnaryRule(tag: Tag, word: Word) extends Rule
case class BinaryRule(tag: Tag, left: Tag, right: Tag) extends Rule

trait Tree {
  def rule: Rule
  def words: List[Word]
  def tags: List[Tag]
  def rules: Iterable[Rule] = binaryRules ++ unaryRules
  def binaryRules: Iterable[BinaryRule]
  def unaryRules: Iterable[UnaryRule]
  def mapWords(f: Word => Word): Tree
  def mkString: String = mkString()
  def mkString(indent: String = ""): String
}
case class Leaf(tag: Tag, word: Word) extends Tree {
  val rule = UnaryRule(tag, word)
  val words = List(word)
  val tags = List(tag)
  def unaryRules = Some(rule)
  def binaryRules = None
  def nodes = Nil
  def leaves = this :: Nil
  def mapWords(f: Word => Word) = Leaf(tag, f(word))
  def mkString(indent: String = ""): String = s"$indent$tag - $word"
}
case class Node(tag: Tag, left: Tree, right: Tree) extends Tree {
  val rule = BinaryRule(tag, left.rule.tag, right.rule.tag)
  def words = left.words ::: right.words
  def tags = tag :: left.tags ::: right.tags
  def unaryRules = left.unaryRules ++ right.unaryRules
  def binaryRules = Some(rule) ++ left.binaryRules ++ right.binaryRules
  def mapWords(f: Word => Word) = Node(tag, left.mapWords(f), right.mapWords(f))
  def mkString(indent: String = ""): String = {
    val l = left.mkString(indent + "  ")
    val r = right.mkString(indent + "  ")
    s"$indent$tag\n$l\n$r"
  }
}

object Tree {
  object JsonProtocol extends DefaultJsonProtocol {
    implicit object TreeFormat extends JsonFormat[Tree] {
      override def read(v: JsValue): Tree = v match {
        case JsArray(Vector(JsString(tag), JsString(word))) => Leaf(tag, word)
        case JsArray(Vector(JsString(tag), left: JsArray, right: JsArray)) => Node(tag, read(left), read(right))
      }
      override def write(tree: Tree): JsValue = tree match {
        case Leaf(tag, word) => JsArray(Vector(JsString(tag), JsString(word)))
        case Node(tag, left, right) => JsArray(Vector(JsString(tag), write(left), write(right)))
      }
    }
  }
  def fromJson(json: String): Tree = {
    import JsonProtocol._
    json.parseJson.convertTo[Tree]
  }
  def toJson(tree: Tree): String = {
    import JsonProtocol._
    tree.toJson.compactPrint
  }
}