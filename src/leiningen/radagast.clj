(ns leiningen.radagast
  (:use [leiningen.compile :only [eval-in-project]]))

(defn radagast [project & nses]
  (eval-in-project
   project `(do (require '~'radagast.coverage)
                (binding [radagast.coverage/*ns-must-match*
                          ~(:radagast/ns-whitelist project)]
                  (@(ns-resolve '~'radagast.coverage '~'coverage)
                   ~@nses)))))
