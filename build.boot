(set-env!
 :source-paths   #{"src/clj" "src/cljs"}
 :resource-paths #{"src/clj" "src/cljs"}
 :dependencies '[[com.draines/postal "2.0.2"]
                 [org.clojure/tools.logging "0.3.1"]
                 [cljs-ajax "0.6.0"]
                 [org.danielsz/om-flash-bootstrap "0.1.0-SNAPSHOT"]])

(task-options!
 push {:repo-map {:url "https://clojars.org/repo/"}}
 pom {:project 'org.danielsz/telefunken
      :version "0.1.0-SNAPSHOT"
      :scm {:name "git"
            :url "https://github.com/danielsz/telefunken"}})

(deftask build
  []
  (comp (pom) (jar) (install)))

(deftask push-release
  []
  (comp
   (build)
   (push)))
