(ns telefunken.contact-form
  (:require [telefunken.core :refer [email]]))

(defn email-form-handler [{{from :from subject :subject body :body} :params :as req}]
  (future (email (System/getProperty "telefunken.contact.email") subject body :reply-to from))
  {:status 200 :headers {} :body from})

(def contact-endpoint ["/telefunken"
                       ["/contact" {:post email-form-handler}]])
