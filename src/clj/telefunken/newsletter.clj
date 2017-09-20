(ns telefunken.newsletter
  (:require
   [twitter-fu.common.crypto :as crypto]
   [detijd.core :as t]
   [environ.core :refer [env]]
   [ring.util
    [codec :refer [url-encode]]
    [response :as util]]))

(defn endpoints [_]
  (routes
   (context "/unsubscribe" []
     (GET "/" [] (html/index))
     (GET "/:email/:expiration/:signature" req (redirect req)))))

(defn redirect [{headers :headers session :session {email :email expiration :expiration signature :signature} :params :as req}]
  (let [valid-signature? #(= signature (crypto/sign (crypto/decode-base64 (:twitter-fu-symmetric-key env) :key) (str email "/" expiration)))]
    (if (valid-signature?)
      (let [email (crypto/decode-base64-url email)
            expiration (crypto/decode-base64-url expiration)
            session (assoc session :unsubscribe {:email email :expiration expiration})]
        (-> (util/redirect (str "http://" (get headers "host") "/unsubscribe"))
            (assoc :session session)))
      (-> (util/redirect (str "http://" (get headers "host")))
          (assoc :session {:authentication-error "Signature invalid."})))))

(defn generate-unsubscribe-link [host email]
  (let [expiration (crypto/encode-base64-url (t/a-month-from-today-str))
        email (crypto/encode-base64-url email)
        signature (crypto/sign (crypto/decode-base64 (:twitter-fu-symmetric-key env) :key) (str email "/" expiration))]
    (str host "/unsubscribe/" email "/" expiration "/" signature)))