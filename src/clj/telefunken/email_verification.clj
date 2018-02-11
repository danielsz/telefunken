(ns telefunken.email-verification
  (:require
   [kryptos.core :as crypto]
   [detijd.core :as t]
   [environ.core :refer [env]]
   [compojure.core :refer [GET routes context]]
   [ring.util
    [codec :refer [url-encode]]
    [response :as util]]))

(defn redirect [{headers :headers session :session {email :email expiration :expiration signature :signature} :params :as req}]
  (let [valid-signature? #(= signature (crypto/sign (crypto/decode-base64 (:telefunken-symmetric-key env) :key) (str email "/" expiration)))]
    (if (valid-signature?)
      (let [email (crypto/decode-base64-url email)
            expiration (crypto/decode-base64-url expiration)
            session (assoc session :verify {:email email :expiration expiration})]
        (-> (util/redirect (str "http://" (get headers "host") "/verify"))
            (assoc :session session)))
      (-> (util/redirect (str "http://" (get headers "host")))
          (assoc :session {:authentication-error "Signature invalid."})))))

(defn endpoints [_]
  (routes
   (context "/verify" []
     (GET "/:email/:expiration/:signature" req (redirect req)))))

(defn generate-verify-link [host email]
  (let [expiration (crypto/encode-base64-url (t/a-month-from-today-str))
        email (crypto/encode-base64-url email)
        signature (crypto/sign (crypto/decode-base64 (:twitter-fu-symmetric-key env) :key) (str email "/" expiration))]
    (str host "/verify/" email "/" expiration "/" signature)))
