(ns radagast.coverage
  (:require [clojure.test]))

(def ^{:dynamic true} *ns-must-match* nil)

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

(defn -main [whitelist & test-nses]
  (binding [*ns-must-match* (re-pattern whitelist)]
    (doseq [n test-nses]
      (require (symbol n)))

    (let [impl-nses (remove skip-ns? (all-ns))]
      (instrument-nses impl-nses)

      (with-out-str
        (apply clojure.test/run-tests (map symbol test-nses)))

      (if-let [uncovered-symbols (uncovered impl-nses)]
        (do (println "Missing test coverage for:")
            (doseq [v uncovered-symbols]
              (println v))
            (System/exit 1))
        (System/exit 0)))))
