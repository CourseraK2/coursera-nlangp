# Discounting

*Week 1*

Maximum likelihood estimates are going to systematically over-estimate the likelihood of seeing a bigrams that appeared in the training corpus (particularly for low count items), and underestimate the likelihood of those that did not.

Therefore we might apply a discount of (say) 0.5 to the observed counts, `count*(x) = count(x) - 0.5`

	            |             |                        |              | 
	x           | count(x)    | q_ML( w_i | w_i-1)     | count*(x)    | count*(x) / count(the)
	---------------------------------------------------------------------------------------------
	the         | 48          |                        |              |
	            |             |                        |              | 
	the, dog    | 15          | 15/48                  | 14.5         | 14.5/48
	the, woman  | 11          | 11/48                  | 10.5         | 11.5/48
	...
	etc

When is comes to summing all of the likelihood estimates there will be some missing probability mass, specifically `0.5 * number_of_rows_in_table`. We can then allocate this 'left-over' probability `alpha` to other words not seen to follow `the` in the training corpus. This is known as a *Katz Back-Off Model*.

### Katz Back-Off Model (Bigrams)

For each word `w` define 2 sets: 

* `A` contains all words that follow `w` at least once in the training data
* `B` contains all words that do not follow `w` in the training data

We can then define `q_BO` (the likelihood of `w_i` following `w_i-1`) as follows:

	q_BO( w_i | w_i-1 )  =  count*( w_i-1, w_i ) / count( w_i-1 )                      if w_i is in A(w_i-1)
	                     =  alpha( w_i-1 ) * ( q_ML( w_i ) / sum[over B]( q_ML( w ) ))    if w_i is in B(w_i-1)

That is:

* for words in `A`, simply use the discounded maximum likelihood. 
* for words in `B`, use previous word's `alpha`, weighted by how common the previous word is releative to the rest of `B`.

### Exercise

Given corpus:

	the book STOP
	his house STOP

and discount 0.5, ie `c*(v, w) = c(v, w) - 0.5`, what is the value of `q_BO( book | his )`?

	For his, A = { house }, B = { the, book, STOP, his }

	q_BO( book | his ) = alpha( his ) * q_ML( book ) / ( q_ML( the ) + q_ML( book ) + q_ML( STOP ) )

	alpha(his) = 1 - (0.5 / 1) = 0.5

	q_ML( the )  = 1/6
	q_ML( book ) = 1/6
	q_ML( STOP ) = 2/6
	q_ML( his ) = 1/6

	So, 

	q_BO( book | his ) = 0.5 * (1/6) / (1/6 + 1/6 + 2/6 + 1/6)
	                   = 0.1

### Katz Back-Off Model (Trigrams)

Thee Katz model for trigrams buils upon the bigram model: `alpha` is weighted by `q_BO` calculated at the bigram level.

	If w_i is in B(w_i-2, w_i-1)

	q_BO( w_i | w_i-2, w_i-1 )  =  (alpha( w_i-2, w_i-1 ) * q_BO( w_i | w_i-1 ) ) / sum[over B]( q_BO( w | w_i-1 ) ) 

### Choosing the discount value

This is generally a value between 0 and 1. A value of 0.5 is a reasonable guess; a better value can be found by optimizing on validation data in a similar way to the `lambda`s in linear interpolation.