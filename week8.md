# Log Linear Models for Tagging

We can also use log-linear models to solve the tagging problem. 

If we have input sentence `w_[1:n] = w_1, w_2 ... w_n` and tag sequence `y_[1..n] = y_1, y_2 ... y_n` we can use a log-linear model to define

	p(t_1, t_2 ... t_n | w_1, w_2 ... w_n)

for any sentence and tag sequence of the same length.

Note the contrast with the HMM which instead defines `p(t_1, t_2 ... t_n, w_1, w_2 ... w_n)`.

Then the most likely tag sequence for `w_[1:n]` is

	t*_[1:n] = arg max[t_[1:n]] p(t_[1:n] | w_[1:n])

How to model `p(t_[1:n] | w_[1:n])`?

	p(t_[1:n] | w_[1:n]) = p(t_1 | w_1, ..., w_n) *                   //By chain rule
	                       p(t_2 | w_1, ..., w_n, t_1) * .
	                       .. * 
	                       p(t_n | w_1, ..., w_n, t_1, ..., t_n-1)

	                     = p(t_1 | w_1, ..., w_n, t_-1, t_0) *        //By trigram independence assumption
	                       p(t_2 | w_1, ..., w_n, t_0, t_1) * .
	                       .. * 
	                       p(t_n | w_1, ..., w_n, t_n-2 t_n-1)

### Representation: Histories

A *history* is a 4-tuple `(t_-2, t_-1, w[1:n], i)` where :

* `t_-2`, `t_-1` are the 2 previous tags
* `w_[1:n]` are the `n` words in the input sentence
* `i` is the index of the word being tagged

`X` is the the set of all possible histories.

`Y` is the set of all possible tags `Y = {NN, NNS, Vt, ...}`. We also have `m` features `f_k: X * Y -> R` for `k=1..m`.

Example features:

	f_1(h, t) = 1 if w_i=base and t=Vt            //effectively e(base|Vt) in HMM
	            0 otherwise
	f_2(h, t) = 1 if w_i ends in ing and t=VBG    //impossible for HMM to capture
	            0 otherwise

(Ratnaparki, 96) defines the following set of features:

* word/tag features for all word/tag pairs, eg `if w_i=base and t=Vt`
* spelling features for all prefixes/suffixes of length <= 4, eg `if w_i ends in ing and t=VBG`
* contextual features, eg trigram/bigram/unigram, `if w_i-1=the and t=Vt`

This set of features is much more powerful than an HMM and is very close to still being the state of the art.

### The Viterbi Algorithm

The **Viterbi Algorithm** is constructed and justified in a similar way to with HMMs. Define:

	r(t_1,...,t_k) = mult[i=1..k] q(t_i | t_i-2, t_i-1, w_[1:n], i)

and dynamic programming table `pi(k, u, v)` as maximum probability of a tag sequence ending in tags `u`, `v` at position `k`.

#### Base Case

	pi(0,*,*) = 1 //Identical to HMM algo

#### Recursive Case

For any `k` in `{1..n}`, for any `u` in `S_k-1` and `v` in `S_k`:

	pi(k,u,v) = max[t in S_k-2] pi(k-1,t,u) *  q(v | t,u,w_[1:n],k)	

`q(v | t,u,w_[1:n],k)` is a log linear model (in place of `q(v | t,u) * e(w_k | v)` in HMM).