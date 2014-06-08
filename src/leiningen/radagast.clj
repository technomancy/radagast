(ns leiningen.radagast
  (:use [leiningen.core.eval :only [eval-in-project]])
  (:import [java.io File]))

(defn radagast [project & nses]
  (let [tempf (File/createTempFile "radagast" ".out")]
    (.deleteOnExit tempf)
    (eval-in-project
     project `(binding [radagast.coverage/*ns-must-match*
                        ~(:radagast/ns-whitelist project)
                        radagast.coverage/*out-file*
                        ~(.getAbsolutePath tempf)]
                (radagast.coverage/coverage ~@nses))
     '(require 'radagast.coverage))
    (->> (.getAbsolutePath tempf)
         slurp
         Integer/parseInt
         System/exit)))
