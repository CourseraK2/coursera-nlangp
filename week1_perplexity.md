# Perplexity

**Perplexity** is a measure of the effectiveness of a language model on a test corpus.

Test corpus is composed of sentences `s1...sm`. We can use our model to calculate the probability of seeing this corpus: `p(s1) * ... * p(sm)`.

Note that:

	log2( p(s1) * ... * p(sm) ) == log2( p(s1) ) + ... + log2( p(sm) )

If we divide the sum above by the number of test sentences `M` we get a 'normalized' value `l` - a measure of the 'probability-per-word' for the test data. Then:

	perplexity = 2^(-l)

where

	l = ( log2( p(s1) ) + ... + log2( p(sm) ) ) / M

So the lower the perplexity, the better the model. 

For the simplest possible model (one that assigns uniform probability to all possible trigrams) the perplexity works out to be `N` (size of vocabulary + 1).

### Exercise

Given trigram language model:

	q(the | *, *)         = 1
	q(dog | *, the)       = 0.5
	q(cat | *, the)       = 0.5
	q(walks | the, cat)   = 1
	q(STOP | cat, walks)  = 1
	q(runs | the, dog)    = 1
	q(STOP | dog, runs)   = 1

and test corpus `the dog runs STOP, the cat walks STOP, the dog runs STOP`, what is the perplexity of the language model?

(Note that the number of words in this test corpus `M` is 12.)

	the dog runs STOP     = 1 * 0.5 * 1 * 1   = 0.5
	the cat walks STOP    = 1 * 0.5 * 1 * 1   = 0.5
	the dog runs STOP     = 1 * 0.5 * 1 * 1   = 0.5

	l = log2(0.5) + log2(0.5) + log2(0.5)) / 12 = -0.25

	perplexity = 2^-( -0.25 ) = 1.189

### Some Context

From Goodman ("A bit of progress in language modeling"), vocabulary size was 50000. Trigram model resulted in perplexity of 74, ie vastly better than base case of 50000. Bigram model resulted in perplexity of 137, unigram 955.

Shannon found that a human modeller performed better than any trigrams, 4-grams etc.