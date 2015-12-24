; Written by Amin Razavi , Dec 2015
; Amirkabir University of Technology (Tehran Polytechnic)

(ns core
  (:require [clojure.math.numeric-tower :only (abs) :as tower]) 
  (:require [clojure.set :only (difference) :as Set])
  (:gen-class))

; Produce a Random Gene
(defn init-gene [NumQueens GENE]
  (reduce #(assoc % %2 (rand-int NumQueens)) GENE (range (count GENE))))

; [It's Optional] Placing two queens in the same place is not allowed (at least at the beginning!)
(defn validate-chromosome [chromosome] 
  (let [SetRange (set (range (count chromosome)))]
    (loop [distchrom (distinct chromosome)]
      (if (= (count distchrom) (count chromosome)) (vec (sort distchrom))
          (recur (conj distchrom [(first (Set/difference SetRange (set (map first  distchrom))))
                                  (first (Set/difference SetRange (set (map second distchrom))))]))))))
                              
; Initialize Genes in the Chromosome
(defn init-chromosome [chromosome]
  (let [GENE (first chromosome)
	NUMQ (count chromosome)]
    (reduce #(assoc % %2 (init-gene NUMQ GENE)) chromosome (range (count chromosome)))))

(defn codiagonal? [q1 q2]
  (if ( = (tower/abs (- (first q1 ) (first q2))) (tower/abs (- (second q1) (second q2)))) true false))

(defn cocolumn? [q1 q2]
  (if (= (first q1) (first q2)) true false))

(defn corow? [q1 q2]
  (if (= (second q1) (second q2)) true false))

(defn fitness [chromosome]
  (let [N (count chromosome)]
	(reduce +
		(for [i (range N) j (range N) :when (> j i)]
			(let [q1 (get chromosome i) q2 (get chromosome j)]
				(if (or (= q1 q2) (corow? q1 q2) (cocolumn? q1 q2) (codiagonal? q1 q2)) 1 0))))))

(defn update-fitness [chromosome]
  (let [fc (first chromosome)]
    (vector fc (fitness fc))))

(defn init-population [population]
  (let [RN (range (count population)) 
        ipop (reduce #(update-in % [%2 0] init-chromosome) population RN)]
    (vec (sort-by #(second %) < (reduce #(update % %2 update-fitness) ipop RN)))))


; sequence of partial sum of Fitnesses (last number in the seq is sum of all fitnesses)
(defn SumFit [population]
  (let [Fits (map second population)]
    (rest (reduce #(conj % (+ %2 (last %))) [0] Fits))))

; Probability of selection is proportional to fitness
(defn ChooseParent [population]
  (let [SFQ (reverse (SumFit population))
	RND (rand-int (first SFQ))]
	(dec (count (take-while #(> % RND) SFQ)))))

; an Auxiliary function for selecting parents
(defn AuxSelectParent [population]
  (let [P1 (ChooseParent population) 
        P2 (ChooseParent population)]
    (if (= P1 P2) [P1 (ChooseParent population)] [P1 P2])))

; n pairs of parents will be choosen
(defn Select-Parents [population n] 
  (repeatedly n #(AuxSelectParent population)))

(defn Select-Mutants [population PopNum MutationRate]
   (if (zero? MutationRate) [(rand-int PopNum)]
        (vec (take MutationRate (repeatedly #(rand-int PopNum))))))

; Mutate a chromosome (random gene & random value for coordinates)
(defn mutate [chromosome]
  (let [genes (first chromosome)
        N (count genes) 
        rndgene (rand-int N) 
	rndcoordinates [(rand-int N) (rand-int N)] ]
    (update-fitness [(assoc genes rndgene rndcoordinates) 28])))

; Cross-over two chromosomes
(defn xover [chrom1 chrom2]
  (let [N (count chrom1)
		node (inc (rand-int N)) cnode (- N node)
        intercombine (concat (take node chrom1) (take-last cnode chrom2) (take-last cnode chrom1) (take node chrom2))] 
    [[(vec (take N intercombine)) 0]
     [(vec (take-last N intercombine)) 0]]))

; generate children
(defn Children [population Parents]
  (reduce into [] (map #(xover (get-in population [(first %) 0]) (get-in population [(second %) 0])) Parents)))

; Produce New (Crossover) Children + Old Population  
(defn New-Population [population XoverRate]
  (into population 
        (map update-fitness 
              (Children population (Select-Parents population XoverRate)))))

; Apply Mutation Operator on the New Populatuion
(defn Mutate-Population [population PopNum MutationRate]
  (vec (sort-by #(second %) < (reduce #(update % %2 mutate) population (Select-Mutants population PopNum MutationRate)))))

; if the boolean "dynamic" is true  => population will grow with XOVER-RATE
; if the boolean "dynamic" is false => population will remain CTEPOP
(defn Generation [population PopNum XoverRate MutationRate dynamic CTEPOP]
  (let [mutedpop (Mutate-Population (New-Population population XoverRate) PopNum MutationRate)]
    (if (true? dynamic) mutedpop (vec (take CTEPOP mutedpop)))))

; MAIN LOOP
(defn -main [& args]
  (if (empty? args)
    (print "Wrong Input arguments! \n USAGE : 'Number of Queens' 'Number of Chromosomes' 'Crossover-Rate[0.0-1.0]' 'Mutation Rate[0.02-0.1]'\n")
    (let [[NUM-QUEENS NUM-CHROMOSOMES XOVER-RATE MUTATION-RATE] (map read-string args)
           XR (* XOVER-RATE NUM-CHROMOSOMES)
           MR (* MUTATION-RATE NUM-CHROMOSOMES)
           MAXFIT (/ (* NUM-QUEENS (dec NUM-QUEENS)) 2)
           GENE [0 0] ; Gene  = Position Coordinates of each queen on the board
           CHROMOSOME (vec (repeatedly NUM-QUEENS #(vec GENE))) ; Chromosome  = Board Configuration = Positions of Queens on the Board
           POPULATION (vec (repeatedly NUM-CHROMOSOMES #(vec [CHROMOSOME MAXFIT])))]
      (time 
       (loop [i 1 NewPop (Generation (init-population POPULATION) NUM-CHROMOSOMES XR MUTATION-RATE false NUM-CHROMOSOMES)]
         (if (zero? (second (first NewPop)))
           (print "GOT IT! at" i "th iteration:" (first (first NewPop)) "population=" (count NewPop) "\n\t=>")
           (if (apply = (map first (take 5 NewPop)))
             (print "LOCAL MIN! at" i "th iteration, fitness value=" (second (first NewPop)) "population=" (count NewPop) "\n\t=>")
             (if (> i 500) 
               (print "TIMEOUT! fitness value=" (second (first NewPop)) "population=" (count NewPop) "\n\t=>")
               (recur (inc i) (Generation NewPop NUM-CHROMOSOMES XR MR false NUM-CHROMOSOMES))))))))))

; Functional Version of Main
; (first (filter #(zero? (second (first %))) (iterate #(Generation % 0.2 0.2 false 10) (init-population p))))
