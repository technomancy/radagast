(ns radagast.coverage
  (:require [clojure.test]))

(defn skip-ns?
  [pattern n]
  (let [name (str (.getName n))]
    (or (re-find #"(^clojure\.|radagast)" name)
        (not (re-find pattern name)))))

(defn- instrument!
  [f v]
  (fn [& args]
    (alter-meta! v assoc ::untested? false)
    (apply f args)))

(defn- safe-to-instrument?
  [v]
  (and (.isBound v)
       (fn? @v)
       (not (:macro (meta v)))
       (not (:test (meta v)))))

(defn instrument-var! [v]
  (when (safe-to-instrument? v)
    (alter-meta! v assoc ::untested? true)
    (alter-meta! v assoc ::original @v)
    (alter-var-root v instrument! v)))

(defn uninstrument-var! [v]
  (when (safe-to-instrument? v)
    (let [root (::original (meta v))]
      (assert root "No root binding to restore!")
      (alter-meta! v dissoc ::untested?)
      (alter-meta! v dissoc ::original)
      (alter-var-root v (constantly root)))))

(defn doto-ns [f ns]
  (doseq [[_ v] (ns-publics ns)]
    (f v)))

(defn instrument-ns! [& nss]
  (doseq [ns nss]
    (doto-ns instrument-var! ns)))

(defn uninstrument-ns! [& nss]
  (doseq [ns nss]
    (doto-ns uninstrument-var! ns)))

(defn uncovered-vars [nses]
  (for [n nses [_ v] (ns-publics n)
        :when (::untested? (meta v))]
    v))

(defn evaluate-test-coverage
  [pattern test-nses]
  (let [test-nses (map symbol test-nses)
        nses (remove (partial skip-ns? pattern) (all-ns))]
    (doseq [n test-nses]
      (require n))
    (try
      (doseq [ns nses]
        (instrument-ns! ns))
      (with-out-str
        (apply clojure.test/run-tests test-nses))
      (uncovered-vars test-nses)
      (finally
        (mapv uninstrument-ns! nses)))))

(defn -main [whitelist & test-nses]
  (let [pattern (re-pattern whitelist)]
    (when-let [vars (seq (evaluate-test-coverage pattern test-nses))]
      (println "Missing test coverage for:")
      (doseq [v vars]
        (println v))
      (System/exit 1))
    (println "All covered.")
    (System/exit 0)))
