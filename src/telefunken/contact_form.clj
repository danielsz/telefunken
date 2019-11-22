(ns telefunken.contact-form
  (:require [telefunken.core :refer [email]]
            [compojure.core :refer [POST routes]]))

(defn email-form-handler [from subject body]
  (future (email (System/getProperty "telefunken.contact.email") subject body :reply-to from))
  {:status 200 :headers {} :body from})

(defn endpoint [_]
  (routes
   (POST "/contact" [from subject body] (email-form-handler from subject body))))
