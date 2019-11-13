(ns timscript.core
  (:require timscript.asr
            [clojure.core.match :refer [match]])
  (:import [timscript.asr
            Id Num IdNum Note Rest Chord Sequence Name Transpose Loop Glue
            Split KeyChange Replace Reverse KeySignature ReplaceValues
            ReplaceValue])
  (:gen-class))

(defn parse
  "Produces an Musical Expression (ME) from a list of arguments"
  [args]
  (if (some (partial = 'and) args)
      (let [split (split-with (partial not= 'and) args)
            [_ & afterAnd] (second split)]
        (Glue. (parse (first split)) (parse afterAnd)))
      (match [args]
        [(['Note (note-id :guard symbol?)
               (vol :guard number?)
               (dur :guard number?)] :seq)]
         (Note. note-id vol dur)

        [(['Rest (dur :guard number?)] :seq)] (Rest. dur)
        [([(['Rest (dur :guard number?) & r] :seq)] :seq)] (Sequence. (Rest. dur) (parse [r]))
        [(['Chord (notes :guard coll?)] :seq)]
        (if (= 'Note (first notes))
          (Chord. (parse (take 4 notes)) (parse ['Chord (drop 4 notes)]))
          (if (= 'Rest (first notes))
            (Chord. (parse (take 2 notes)) (parse ['Chord (drop 2 notes)]))
            nil))

        [([(['Note
            (note-id :guard symbol?)
            (vol :guard number?)
            (dur :guard number?) & r] :seq)] :seq)] (Sequence. (Note. note-id vol dur) (parse [r]))

        [(['Rest (dur :guard number?) & r] :seq)] (Sequence. (Rest. dur) (parse r))
        [(['reverse & r] :seq)] (Reverse. (parse r))
        [(['transpose 'by (dist :guard number?) & r] :seq)] (Transpose. (Num. dist) (parse r))
        [(['loop (times :guard number?) 'times & r] :seq)] (Loop. (Num. times) (parse r))
        [(['split (start :guard number?) (end :guard number?) & r] :seq)]
        (Split. (Num. start) (Num. end) (parse r))

        [(['let (bound-id :guard symbol?) '= & r] :seq)] (Name. (Id. bound-id) (parse r))
        [([(bound-id :guard symbol?) ([(value :guard number?)] :seq)] :seq)]
        (IdNum. (Id. bound-id) (Num. value))

        [([(bound-id :guard symbol?)] :seq)] (Id. bound-id)
        [([([] :seq)] :seq)] nil
        :else (println "Unable to parse expression" args))))

(defn interp
  "Interpret the mexpr, producing the final mexpr"
  [mexpr]
  (cond
    (instance? Note mexpr) (println "A note!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (interp (parse (read-string (str "(" (first args) ")")))))
