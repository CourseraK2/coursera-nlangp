# Generative Models for Supervised Learning

The **Tagging Problem** is the problem of tagging (for example) words in a sentence with the appropriate parts of speech (noun, verb etc).

For a **Supervised Learning** problem we have training examples `x_i`, `y_i` for `i=1..m`. Each `x_i` is an input, `y_i` is a label. The goal is to learn a function `f` that maps inputs to labels.

eg, with the tagging problem,

	x_1 = the dog laughs  y_1 = DT NN VB //DT = Determiner, NN = Noun, VB = Verb
	x_2 = the cat barks   y_2 = DT NN VB
	etc...

### Conditional (or Discriminative) Models

* Learn a distribution `p(y|x)` from training examples.
* For any test input `x`, define `f(x) = argmax_y p(y|x)`. ie `f(x)` is the most likely `y` according to the learned distribution.

### Generative Models

Learn distribution `p(x, y)`. ie the *joint distribution* of `x` and `y`; the probability of `x` and `y`. 

Often `p(x, y) = p(y)p(x|y)`. This means that given `p(x, y)` we can easily calculate the conditional distribution using Bayes Rule: 

	p(y|x) = ( p(y)p(x|y) ) / p(x)
	       = p(x, y) / p(x)

Output from the model is: 

	f(x) = argmax_y p(y|x)

	     //By substitution using Bayes Theorem (as above)
	     = argmax_y ( p(y)p(x|y) ) / p(x)

	     //Because p(x) does not vary with y
	     = argmax_y p(y)p(x|y)

The last term is useful as `p(x)` can often be hard to calculate.

# Hidden Markov Models

Input is sentence `x = x_1, x_2, ..., x_n`, eg `the dog`.

Label is sequence `y = y_1, y_2, ..., y_n`, eg `DT NN`.

We will define (in a moment) HMM `p(x_1, ..., x_n, y_1, ..., y_n)` for any sentence and tag sequence of same length. Using this, the most likely tag sequence for a given input `x` is `argmax[y_1...y_n] p(x_1, ..., x_n, y_1, ..., y_n)` - ie. the `y` that maximises the HMM output. Note that brute force search over this space is unlikely to be possible - will see a way round this for HMMs later.

### Trigram Markov Model

For sentence `x_1...x_n` where every `x_i` is in `V` (the vocabulary), and tag sequence `y_1...y_n+1` where `y_i` is in `S` (the set of all tags) and `y_n+1` is `STOP`, the joint probability of the sentence and the tag sequence is:

	p(x_1, ..., x_n, y_1, ..., y_n) = q(y_1 | y_-1, y_0) *    // Trigram (transition) parameters eg q(DT | NN, VT)
	                                  ... *                   // 
	                                  q(y_n+1 | y_n-1, y_n) * // 
	                                  e(x_1 | y_1) *          // Emission parameters. eg e(the | DT)
	                                  ... *                   //
	                                  e(x_n | y_n)            //

#### Exercise

Say we have tag set `S = {D, N}`, vocabulary `V = {the, dog}` and a hidden Markov Model with transition parameters:

	q(D | *, *) = 1
	q(N | *, D) = 1
	q(STOP | D, N) = 1
	q(s | u, v) = 0 for all other q params

and emission parameters:

	e(the | D) = 0.9
	e(dog | D) = 0.1
	e(dog | N) = 1

How many pairs of sequences `x_1, ..., x_n, y_1, ..., y_n+1` satisfy `p(x_1, ..., x_n, y_1, ..., y_n+1) > 0`?

	p(the, D)         = q(D | *, *) * q(STOP | *, D) * e(the | D) 
	                  = 1 * 0 * 0.9
	                  = 0 // INVALID. Same for all other 1-word sentences as STOP term will be 0
	
	p(the, the, D, N) = q(D | *, *) * q(N | *, D) * q(STOP | D, N) * e(the, D) * e(the, N)
	                  = 1 * 1 * 1 * 0.9 * 0
	                  = 0 // INVALID. Same for all other sentences where the is tagged as N
	
	p(the, dog, D, N) = q(D | *, *) * q(N | *, D) * q(STOP | D, N) * e(the, D) * e(dog, N)
	                  = 1 * 1 * 1 * 0.9 * 1
	                  = 0.9 
	
	p(dog, dog, D, N) = q(D | *, *) * q(N | *, D) * q(STOP | D, N) * e(dog, D) * e(dog, N)
	                  = 1 * 1 * 1 * 0.1 * 1
	                  = 0.1 

Notice how result is zero whenever either an illegal transition or an invalid word/tag pair features.

### Parameter Estimation

`q` can be estimated using using maximum likelihood with linear interpolation (as seen in week 1), for example:

	q(Vt | DT, JJ) = lambda_1 * ( count(Dt, JJ, VT) / count(Dt, JJ) ) +   // Trigram ML estimate
	                 lambda_2 * ( count(JJ, Vt) / count(JJ) ) +           // Bigram ML estimate
	                 lambda_3 * ( count(Vt) / count() )                   // Unigram ML estimate

Where `lambda_1 + lambda_2 + lambda_3 = 1` and `lambda_i > 0` for all `i`.

`e` can be estimated very simply, again using maximum likelihood:

	e(base | Vt)   = count(Vt, base) / count(Vt)

#### Exercise

Consider the following corpus of tagged sentences:

	the dog barks -> D N V STOP
	the cat sings -> D N V STOP

What is the value of the parameter `e(cat | N)` for an HMM estimated on this corpus?

	e(cat | N) = count(N, cat) / count(N)
	           = 1 / 2
	           = 0.5

If we use linear interpolation with `lambda_1 = lambda_2 = lambda_3 = 1/3`, what is the value of the parameter `q(STOP | N, V)`?

	q(STOP | N, V) = 1/3 * ( count(N, V, STOP) / count(N, V) ) +
	                 1/3 * ( count(V, STOP) / count(V) ) +
	                 1/3 * ( count(STOP) / count() )
	               = 1/3 * ( 2 / 2 ) + 1/3 * ( 2 / 2 ) + 1/3 * (2 / 8)
	               = 0.75

### Dealing with Low Frequency Words

Estimating `e` is complicated by the fact that `e(y | x) = 0` for all `y` if `x` is not seen in the training data. In other words, we are unable to tag words that we have not seen in training (which can happen frequently in practice).

A common method used to solve this is:

1. Split vocabulary into 2 sets: 
	* Frequent words - that appear eg 5+ times
	* Low frequency words - all other words

2. Map low frequency words into a small, finite set of tokens, eg `twoDigitNum`, `allCaps`, `capPeriod` etc. Then run the model on the mapped/tokenized dataset.
