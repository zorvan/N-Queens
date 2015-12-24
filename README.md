# N-Queens

Solving N-Queens Problem Using Genetic Algorithm in Clojure

## Usage
 
 ------- RUN METHOD --------
 ```shell
 > lein run 8 200 0.3 0.03

 > lein run 
 Wrong Input arguments!
 USAGE : 'Number of Queens' 'Number of Chromosomes' 'Crossover-Rate[0.0-1.0]' 'Mutation Rate[0.02-0.1]'
 ```
 
 ------- DEBUG METHOD 1-------
 ```shell
 > lein repl
 > (load "core")
 > (-main "8" "200" "0.3" "0.03")
 ```
 ------- DEBUG METHOD 2-------
 1. open "core.clj" in emacs
 2. run cider (C-c M-j)
 3. evaluate buffer (C-c C-k)
 4. (-main "8" "200" "0.3" "0.03")
 
 *NOTE*  you may need to evaluate it multiple time to get the answer.
 
 --------- SAMPLE RESULTS ------------------
 
TIMEOUT! fitness value= 55/2 population= 5000 
	=>"Elapsed time: 24605.194236 msecs"

GOT IT! at 12 th iteration: [[3 5] [2 7] [4 2] [5 6] [6 1] [1 0] [7 3] [0 4]] population= 5000 
	=>"Elapsed time: 14089.947256 msecs"

 --------------------------------------

## License

Copyright © 2015 Amin Razavi

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
