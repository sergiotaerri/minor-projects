(ns clj-spellchecker.core
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s])
  (:import (org.apache.commons.lang3 StringUtils)))

(def correct-words (->> "resources/wordsEn.txt"
                        slurp
                        str/split-lines (map str/trim)
                        set))

(defn correct? [word]
  (contains? correct-words word))

(defn lev-distances [word]
  (mapv (fn [correct-word]
          (vector correct-word (StringUtils/getLevenshteinDistance word correct-word))) correct-words))

(defn filter-corrections [threshold corrections]
  (filter (fn [[_ v]]
            (<= v threshold)) corrections))

(defn print-suggestions [sugs]
  (println "did you mean" (str/join ", " (map first sugs)) "?"))

(defn -main [& words]
  (doseq [word words]
    (if (is-correct? word)
      (prn "correct")
      (do
        (printf "Word %s is incorrect, " word)
        (->> word
             lev-distances
             (filter-corrections 1)
             print-suggestions)))))


(s/def ::word string?)
