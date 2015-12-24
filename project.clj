(defproject nqueens "0.1.0"
  :description "Solving N-Queens Problem using Genetic Algorithms , Written by Amin Razavi , Dec 2015"
  :url "http://zorvan.github.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.nrepl "0.2.12"]]
  
  :profiles {:dev {:plugins [[cider/cider-nrepl "0.10.0"]]}}
  :main core
)
