package ab.nlp.assignment1

import ab.nlp.Word

/**
 * Created by adam at 16/10/2014 11:20
 */
class Viterbi(hmm: HMM) {
  /** @return most likely tag sequence for sentence */
  def apply(ws: List[Word]): List[WordTag] = {
    type Params = (Int, Tag, Tag)
    type Choice = (Tag, Double)
    type Table = Map[Params, Choice]
    val n = ws.size
    def S(k: Int): Set[Tag] = {
      if (k <= 0) Set(Tag.*) else hmm.S
    }
    //Builds table of most likely tag at k-2 conditional on subsequent 2 tags
    def buildTable(k: Int, table: Table): Table = {
      if (k > n) table
      else {
        val choices: Set[(Params, Choice)] = for (u <- S(k - 1); v <- S(k)) yield {
          val choices: Set[Choice] = for (w <- S(k - 2)) yield {
            val pi = table((k - 1, w, u))._2 * hmm.q(NGram(w, u, v)) * hmm.e(ws(k - 1), v)
            w -> pi
          }
          val best = choices.maxBy(_._2)
          (k, u, v) -> best
        }
        buildTable(k + 1, table ++ choices)
      }
    }
    //Calculates most likely tag at each position up to n-2...
    val table = buildTable(1, Map((0, Tag.*, Tag.*) ->(Tag.*, 1.0)))
    //Chooses most likely final 2 tags
    val (last0, last1) = {
      val choices: Set[((Tag, Tag), Double)] = for (u <- hmm.S; v <- hmm.S) yield {
        val p = table(n, u, v)._2 * hmm.q(NGram(u, v, Tag.STOP))
        (u, v) -> p
      }
      choices.maxBy(_._2)._1
    }
    //Constructs final sequence
    def buildSequence(k: Int, sequence: List[Tag]): List[Tag] = {
      if (k == 0) sequence
      else {
        val tag = table(k + 2, sequence.head, sequence.tail.head)._1
        buildSequence(k - 1, tag :: sequence)
      }
    }
    val ts = buildSequence(n - 2, List(last0, last1))
    ws zip ts
  }
}
