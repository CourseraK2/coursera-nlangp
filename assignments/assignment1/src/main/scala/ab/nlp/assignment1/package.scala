package ab.nlp

/**
 * Created by adam at 16/10/2014 08:33
 */
package object assignment1 {
  type Tag = String
  type WordTag = (Word, Tag)
  object Tag {
    val * : Tag = "*"
    val STOP: Tag = "STOP"
  }
  type NGram = List[Tag]
  val NGram = List
}
