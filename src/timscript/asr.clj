(ns timscript.asr)

;; Concrete Language Syntax (EBNF) from 4.1
;;
;; <ME> ::=
;;        | <id>
;;        | <id>[<num>]
;;        | <Beat>
;;        | <Sequence>
;;        | <id> = <ME>
;;        | transpose <ME> by <int>
;;        | loop <ME> <int> times
;;        | <andSequences>
;;        | <thenSequences>
;;        | split <Sequence> <num> <num>
;;        | keyChange <Sequence> to <KeySignature>
;;        | replace (<ReplaceValues>) in <Sequence>
;;        | reverse <Sequence>
;;
;; <Sequence> ::=
;;              | <Beat> <Sequence>

;; <andSequences> ::= <Sequence>
;;                  | <Sequence> and <andSequences>
;;
;; <thenSequences> ::= <Sequence>
;;                   | <Sequence> then <thenSequence>
;;
;; <KeySignature> ::= <NoteId> -> <NoteId>
;;                  | <NoteId> -> <NoteId>, <KeySignature>
;;
;; <Beat> ::= Note <Note>
;;          | Rest <num>
;;          | Chord <Chord>
;;
;; <Note> ::= <NoteId> <num> <num>
;;
;; <NoteId> ::= [one of a,b,c,d,e, etc.]
;;
;; <Chord> ::=
;;           | <Note> <Chord>
;;
;; <ReplaceValues> ::=
;;                   | <ReplaceValue> <ReplaceValues>
;;
;; <ReplaceValue> ::= _
;;                  | <Beat>

;; Abstract Syntax Representation

(defprotocol ME)

(defrecord Id [name] ME)
(defrecord IdNum [name num] ME)
(defrecord Note [note-id vol dur] ME)
(defrecord Rest [num] ME)
(defrecord Chord [notes] ME)
(defrecord Sequence [beat rest-beats] ME)
(defrecord Name [bound-id bound-me] ME)
(defrecord Transpose [me distance] ME)
(defrecord LoopMe [me times] ME)
(defrecord AndSeq [seq rest-seqs] ME)
(defrecord ThenSeq [seq rest-seqs] ME)
(defrecord Split [seq start end] ME)
(defrecord KeyChange [seq ks] ME)
(defrecord Replace [repVals seq] ME)
(defrecord Reverse [seq] ME)
(defrecord KeySignature [n1 n2 rest-ks] ME)
(defrecord ReplaceValues [rv rest-rvs] ME)
(defrecord ReplaceValue [value] ME)
