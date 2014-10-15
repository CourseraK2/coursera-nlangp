package ab.nlp.assignment1

import scala.io.Source

/**
 * Created by adam at 15/10/2014 09:13
 */
object Part1 extends App {
  val trainingFile = "/gene.train"
  type Word = String
  type Tag = String
  type WordTag = (Word, Tag)
  val rawWordTags: List[WordTag] = for {
    l <- Source.fromInputStream(getClass.getResourceAsStream(trainingFile)).getLines().toList
    if !l.trim.isEmpty
    w :: t :: Nil = l.split(" ").toList
  } yield (w, t)
  def counts[A](as: List[A]): Map[A, Int] = as.groupBy(a => a).mapValues(_.size)
  val rawWordTagCounts: Map[WordTag, Int] = counts(rawWordTags)
  def totals[A](counts: List[(A, Int)]): Map[A, Int] = {
    counts.groupBy(_._1).mapValues {
      _.map(_._2).sum
    }.toList.toMap //Convert to list & back to get fresh impl as map produced by groupBy seems to be v slow
  }
  val rawWordCounts: Map[Word, Int] = totals(for (((w, t), c) <- rawWordTagCounts.toList) yield w -> c)
  val isCommon: (Word => Boolean) = (for ((w, c) <- rawWordCounts if c >= 5) yield w).toSet
  def toVocab(w: Word): Word = if (isCommon(w)) w else "_RARE_"
  val vocabTags: List[WordTag] = for ((w, t) <- rawWordTags) yield (toVocab(w), t)
  val vocab: Set[String] = vocabTags.map(_._1).toSet
  val vocabTagCounts: Map[WordTag, Int] = counts(vocabTags)
  val tagCounts: Map[Tag, Int] = totals(for (((w, t), c) <- vocabTagCounts.toList) yield t -> c)
  val tags: Set[Tag] = tagCounts.keySet
  val eps: Map[WordTag, Double] = for (((w, t), c) <- vocabTagCounts) yield {
    (w, t) -> (c / tagCounts(t).toDouble)
  }
  val vocabEps: Map[Word, Map[Tag, Double]] = eps.groupBy {
    case ((w, t), ep) => w
  }.mapValues(_.map {
    case ((w, t), ep) => t -> ep
  })
  def mostLikelyTag(w: Word): Tag = {
    val tags = vocabEps(toVocab(w)).toList.sortBy {
      case (t, ep) => ep
    }.reverse
    tags.head._1
  }
  val devFile = "/gene.dev"
  val out = for (l <- Source.fromInputStream(getClass.getResourceAsStream(devFile)).getLines().toList) yield {
    if (l.trim.isEmpty) ""
    else {
      val tag = mostLikelyTag(l)
      s"$l $tag"
    }
  }
  out.foreach(println)
}
