(set-env!
 :source-paths   #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[com.draines/postal "2.0.3"]
                 [org.danielsz/lang-utils "0.1.1"]
                 [org.danielsz/kryptos "0.1.0"]
                 [clj-http "3.10.0"]
                 [environ "1.1.0"]])

(task-options!
 push {:repo-map {:url "https://clojars.org/repo/"}}
 pom {:project 'org.danielsz/telefunken
      :version "0.1.7"
      :scm {:name "git"
            :url "https://github.com/danielsz/telefunken"}})

(deftask build
  []
  (comp (pom) (jar) (install)))

(deftask push-release
  []
  (comp (build) (push)))

(deftask dev-checkout
  []
  (comp (watch) (build)))
