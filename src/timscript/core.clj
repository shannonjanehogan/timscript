(ns timscript.core
  (:require [timscript.asr])
  (:import [timscript.asr
            Id IdNum Note Rest Chord Sequence Name Transpose LoopMe AndSeq
            Split KeyChange Replace Reverse KeySignature ReplaceValues
            ReplaceValue])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println args))
