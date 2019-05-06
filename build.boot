(set-env!
 :source-paths   #{"src/clj" "src/cljs"}
 :resource-paths #{"src/clj" "src/cljs"}
 :dependencies '[[com.draines/postal "2.0.3"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.danielsz/lang-utils "0.1.1"]
                 [org.danielsz/kryptos "0.1.0"]
                 [clj-http "3.9.1"]
                 [environ "1.1.0"]
                 [org.danielsz/cljs-utils "0.1.1"]
                 [com.andrewmcveigh/cljs-time "0.5.0"]
                 [org.clojure/core.match "0.3.0"]
                 [org.danielsz/om-flash-bootstrap "0.1.0"]])

(task-options!
 push {:repo-map {:url "https://clojars.org/repo/"}}
 pom {:project 'org.danielsz/telefunken
      :version "0.1.3"
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
