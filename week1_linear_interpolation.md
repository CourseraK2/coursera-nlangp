# Linear Interpolation

*Week 1*

### Recap on Sparse Data Problems

For trigram models we need to estimate the probability of seeing a word given its 2 predecessors. A natural way to do this is the intuative *maximum likelihood estimate*:

	q_ML( w_i | w_i-2, w_i-1 )  =   count( w_i-2, w_i-1, w_i ) / count( w_i-2, w_i-1 )

For example:

	q_ML( laughs | the, dog )   =   count( the, dog, laughs ) / count( the, dog )

However with vocabulary size `N` this results in `N^3` parameters in the model. For a vocabulary of size 20000 that is 8*10^12 parameters - far too many.

This problem manifests itself with zeros for the numerator (resulting in zero probability estimates) or the denominator (resulting in undefined estimates). 

### Other estimators

We can similarly calculate bigram and unigram estimators:

##### Bigram

	q_ML( w_i | w_i-1 )         =   count( w_i-1, w_i ) / count( w_i-1 )

##### Unigram

	q_ML( w_i )                 =   count( w_i ) / count()

Where `count()` is the number of words in the corpus.

### Bias-Variance Tradeoff

The trigram estimator has low bias (as it contains a good amount of context) so will result in a good probability estimate. However it requires a very large amount of data in order to converge reliably.

Conversely, the unigram estimator has high bias (it will result in a crude estimate only) but low variance (it will converge relatively quickly).

### Combining the Estimates

One way of improving our estimate would be to combine the trigram, bigram and unigram maximum-lihelihook estimates, using some weighting scheme:

	q( w_i | w_i-2, w_i-1 )      =   lambda_1 * q_ML( w_i | w_i-2, w_i-1 ) + 
	                                 lambda_2 * q_ML( w_i | w_i-1 ) + 
	                                 lambda_3 * q_ML( w_i ) + 

where `lambda_1 + lambda_2 + lambda_3 = 1` and `lambda_i > 0` for all `i`.

For example, using `lambda_1 = lambda_2 = lambda_3 = 1/3`:

	q( laughs | the, dog )       =   1/3 * q_ML( laughs | the, dog ) + 
	                                 1/3 * q_ML( laughs | dog) +
	                                 1/3 * q_ML( laughs )

### Exercise

Given the corpus:

	the green book STOP
	my blue book STOP
	his green house STOP
	book STOP

In a language model based on linear interpolation with `lambda_i = 1/3` for all `i in {1, 2, 3}`, what is the value of the parameter `q_LI( book | the, green )` in the model?

	q_LI( book | the, green )    = 1/3 * q_ML( book | the, green ) + 
	                               1/3 * q_ML( book | green ) + 
	                               1/3 * q_ML( book )

	                             = 1/3 * ( count( the, green, book ) / count( the, green ) ) +
	                               1/3 * ( count( green, book ) / count( green ) ) +
	                               1/3 * ( count( book ) / count() )

	                             = 1/3 * ( 1 / 1 ) + 1/3 * ( 1 / 2 ) + 1/3 * ( 3 / 14 ) 

	                             = 0.571

### Choosing Lambdas

These are estimated from the data:

* Hold out a percentage of the training set as "vailidation" data.

* Define `c'(w_1, w_2, w_3)` as the number of times the trigram `(w_1, w_2, w_3)` is seen in the validation set.

* Choose `lambda_1`, `lambda_2`, `lambda_3` to maximise:
		
		L(lambda_1, lambda_2, lambda_3)  =  sum[over w_1,w_2,w_3]( 
		                                      c'(w_1, w_2, w_3) * log( q( w_3 | w_1, w_2 )) 
		                                    )
  (Note that q contains `lambda_1`, `lambda_2`, `lambda_3`.)

  A smaller `q` results in a more negative `log( q )`, which is then compounded by the true number of occurances `c'`. So the worse the `lambda`s are, the more negative the overall sum will be.

### Practical Note

In practice it is generally important to vary the `lambda`s based on how frequently given tri/brigrams appear in the corpus. eg For trigrams that do not appear anywhere in the corpus we would set `lambda_1 = 0` and adjust `lambda_1` and `lambda_2` upwards accordingly.