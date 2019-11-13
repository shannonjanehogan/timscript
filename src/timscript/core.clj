(ns timscript.core
  (:require timscript.asr
            [clojure.core.match :refer [match]])
  (:import [timscript.asr
            Id Num IdNum Note Rest Chord Sequence Name Transpose Loop AndSeq
            Split KeyChange Replace Reverse KeySignature ReplaceValues
            ReplaceValue])
  (:gen-class))

(defn parse
  "Produces an Musical Expression (ME) from a list of arguments"
  [args]
  (match [args]
    [(['Note (note-id :guard symbol?)
           (vol :guard number?)
           (dur :guard number?)] :seq)]
     (Note. note-id vol dur)
    [(['Rest (dur :guard number?)] :seq)] (Rest. dur)
    [(['Note (note-id :guard symbol?)
           (vol :guard number?)
           (dur :guard number?) & r] :seq)]
    (Sequence. (Note. note-id vol dur) (parse r))
    [(['Rest (dur :guard number?) & r] :seq)] (Sequence. (Rest. dur) (parse r))
    [(['reverse & r] :seq)] (Reverse. (parse r))
    [([(bound-id :guard symbol?)] :seq)] (Id. (bound-id))
    :else (println "Unable to parse expression" args)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (parse (read-string (str "(" (first args) ")")))))
