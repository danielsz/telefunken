(ns telefunken.contact-form
  (:require [telefunken.core :refer [email-from]]
            [compojure.core :refer [POST routes]]
            [environ.core :refer [env]]))

(defn email-form-handler [from subject body]
  (future (email-from from subject body :to (or (:telefunken-contact-email env) (:telefunken-email env))))
  {:status 200 :headers {} :body from})

(defn endpoint [_]
  (routes
   (POST "/contact" [from subject body] (email-form-handler from subject body))))
