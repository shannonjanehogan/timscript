(ns timscript.core
  (:require timscript.asr
            [clojure.core.match :refer [match]])
  (:import [timscript.asr
            Id IdNum Note Rest Chord Sequence Name Transpose LoopMe AndSeq
            Split KeyChange Replace Reverse KeySignature ReplaceValues
            ReplaceValue])
  (:gen-class))

(defn parse
  "Produces an Musical Expression (ME) from a list of arguments"
  [args]
  (match [args]
    [[Note (note-id :guard symbol?)
           (vol :guard number?)
           (dur :guard number?)]]
     (Note. note-id vol dur)
    [[Rest (value :guard number?)]] (Rest. value)
    :else (println "Unable to parse expression")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (parse (read-string (str "[" (first args) "]"))))
