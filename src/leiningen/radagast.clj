(ns leiningen.radagast
  (:use [leiningen.core.eval :only [eval-in-project]]))

(defn radagast [project & nses]
  (eval-in-project
   project `(binding [radagast.coverage/*ns-must-match*
                      ~(:radagast/ns-whitelist project)]
              (radagast.coverage/coverage ~@nses))
   '(require 'radagast.coverage)))
