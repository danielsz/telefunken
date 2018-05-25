(set-env!
 :source-paths   #{"src/clj" "src/cljs"}
 :resource-paths #{"src/clj" "src/cljs"}
 :dependencies '[[com.draines/postal "2.0.2"]
                 [org.clojure/tools.logging "0.4.0"]
                 [cljs-ajax "0.6.0"]
                 [org.danielsz/lang-utils "0.1.0-SNAPSHOT"]
                 [org.danielsz/kryptos "0.1.0-SNAPSHOT"]
                 [clj-http "3.7.0"]
                 [environ "1.1.0"]
                 [org.danielsz/cljs-utils "0.1.1"]
                 [com.andrewmcveigh/cljs-time "0.5.0"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.danielsz/om-flash-bootstrap "0.1.0-SNAPSHOT"]])

(task-options!
 push {:repo-map {:url "https://clojars.org/repo/"}}
 pom {:project 'org.danielsz/telefunken
      :version "0.1.2"
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
