(ns telefunken.signed-request
  (:require
   [kryptos.core :as crypto]
   [detijd.core :as t]
   [environ.core :refer [env]]
   [compojure.core :refer [GET routes context]]
   [ring.util
    [codec :refer [url-encode]]
    [response :as util]])
  (:import [java.time Instant]))

(defn redirect [{headers :headers session :session {email :email created_at :created_at signature :signature} :params :as req} route]
  (let [valid-signature? #(= signature (crypto/sign (crypto/decode-base64 (:telefunken-symmetric-key env) :key) (str email "/" created_at)))
        session (assoc session (keyword route) (if (valid-signature?)
                                                 {:email (crypto/decode-base64-url email) :created_at (crypto/decode-base64-url created_at)}
                                                 {:error "Signature invalid."}))]
    (-> (util/redirect (str "http://" (get headers "host") "/" route))
        (assoc :session session))))

(defn create-signed-request-endpoint [route]
  (fn [_]
    (routes
     (context (str "/" route) []
       (GET "/:email/:created_at/:signature" req (redirect req route))))))

(defn generate-signed-request-link [host email route]
  (let [created_at (crypto/encode-base64-url (str (Instant/now)))
        email (crypto/encode-base64-url email)
        signature (crypto/sign (crypto/decode-base64 (:telefunken-symmetric-key env) :key) (str email "/" created_at))]
    (str host "/" route "/" email "/" created_at "/" signature)))

(defn generate-unsubscribe-link [host email]
  (generate-signed-request-link host email "unsubscribe"))
