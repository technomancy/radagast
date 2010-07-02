(ns radagast.coverage
  (:require [clojure.test]))

(def *ns-must-match* nil)

(defn skip-ns? [n]
  (or (re-find #"(^clojure\.|radagast)" (str (.getName n)))
      (and *ns-must-match*
           (not (re-find *ns-must-match* (str (.getName n)))))))

(defn instrument [f v]
  (fn [& args]
    (alter-meta! v assoc ::untested? false)
    (apply f args)))

(defn instrument-nses [nses]
  (doseq [n nses [_ v] (ns-publics n)]
    (when (and (.isBound v) (fn? @v)
               (not (:macro (meta v))) (not (:test (meta v))))
      (alter-meta! v assoc ::untested? true)
      (alter-var-root v instrument v))))

(defn uncovered [nses]
  (for [n nses [_ v] (ns-publics n) :when (::untested? (meta v))] v))

(defn coverage [& test-nses]
  (doseq [n test-nses] (require (symbol n)))
  (let [impl-nses (remove skip-ns? (all-ns))]
    (instrument-nses impl-nses)
    (with-out-str
      (apply clojure.test/run-tests (map symbol test-nses)))
    (println "Missing test coverage for:")
    (doseq [v (uncovered impl-nses)]
      (println v))))
