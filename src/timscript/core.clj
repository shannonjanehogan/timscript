(ns timscript.core
  (:require timscript.asr
            [clojure.core.match :refer [match]])
  (:import [timscript.asr
            Id Num IdNum Note Rest Chord Sequence Name Transpose Loop Glue
            Split KeyChange Replace Reverse KeySignature ReplaceValues
            ReplaceValue])
  (:gen-class))

(def noteSeq
  ['a 'as 'b 'c 'cs 'd 'ds 'e 'f 'fs 'g 'gs])

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

(defn glueSeqs [seq1 seq2]
  "Combines two Sequences into one"
  (cond
    (nil? seq1) seq2
    :else (Sequence. (:beat seq1) (glueSeqs (:rest-beats seq1) seq2))))

(defn reverseSeq [sequ]
  "Reverses a Sequence"
  (cond
    (nil? sequ) sequ
    :else
    (glueSeqs (reverseSeq (:rest-beats sequ)) (Sequence. (:beat sequ) nil))))

(defn transposeMexpr [dist mexpr]
  "Moves the notes of mexpr in direction and distance of dist"
  (cond
    (instance? Note mexpr)
    (let [index (mod (+ dist (.indexOf noteSeq (:note-id mexpr))) (count noteSeq))]
      (Note. (get noteSeq index) (:vol mexpr) (:dur mexpr)))
    (instance? Rest mexpr) mexpr
    (instance? Sequence mexpr) (Sequence. (transposeMexpr dist (:beat mexpr))
                                          (transposeMexpr dist (:rest-beats mexpr)))
    (instance? Chord mexpr) (Chord. (transposeMexpr dist (:note mexpr))
                                    (transposeMexpr dist (:rest-notes mexpr)))))

(defn loopMexpr [times mexpr]
  "Repeats the ME times number of times"
  (cond
    (= 0 times) nil
    (= 1 times) (if (instance? Sequence mexpr) mexpr (Sequence. mexpr nil))
    (or (instance? Note mexpr)
        (instance? Rest mexpr)
        (instance? Chord mexpr))
    (Sequence. mexpr (loopMexpr (- times 1) mexpr))
    (instance? Sequence mexpr) (glueSeqs mexpr (loopMexpr (- times 1) mexpr))))

(defn splitSeq [start end sequ]
  "Removes the first <start> values and values after <end> from sequ"
  (cond
    (nil? sequ) sequ
    (and (< start 0)
         (< end 0)) sequ
    (< start 0) (Sequence. (:beat sequ) (splitSeq start (dec end) (:rest-beats sequ)))
    (< end 0) nil
    :else (Sequence. (:beat (:rest-beats sequ))
                     (splitSeq (dec start) (dec end) (:rest-beats (:rest-beats sequ))))))

(defn interp
  "Interpret the mexpr, producing the final mexpr"
  [mexpr env]
  (cond
    (instance? Note mexpr) mexpr
    (instance? Rest mexpr) mexpr
    (instance? Glue mexpr) (glueSeqs (:sequ mexpr) (:rest-seqs mexpr))
    (instance? Chord mexpr) mexpr
    (instance? Sequence mexpr) mexpr
    (instance? Reverse mexpr) (reverseSeq (:sequ mexpr))
    (instance? Transpose mexpr) (transposeMexpr (:value (:dist mexpr))
                                                (interp (:me mexpr) env))
    (instance? Loop mexpr) (loopMexpr (:value (:times mexpr)) (interp (:me mexpr) env))
    (instance? Split mexpr) (splitSeq (:value (:start mexpr))
                                      (:value (:end mexpr))
                                      (:sequ mexpr))
    (instance? Name mexpr) (println "TODO!")
    (instance? Id mexpr) (println "TODO!")
    (instance? IdNum mexpr) (println "TODO!")
    :else (println "TODO!" mexpr)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (interp (parse (read-string (str "(" (first args) ")"))) [])))
