(ns telefunken.core
  (:require [postal.core :refer [send-message]]
            [postal.support :refer [message-id]])
  (:import java.net.InetAddress))

(defn email? [s]
  (let [regex #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (boolean (re-matches regex s))))

(defn config [] {:host (System/getProperty "telefunken.smtp.host")
                 :user (System/getProperty "telefunken.smtp.user")
                 :pass (System/getProperty "telefunken.smtp.password")
                 :port (Integer. ^String (System/getProperty "telefunken.smtp.port"))})

(defn email [to subject body & {:keys [bcc reply-to]}]
  (send-message (config)
                {:from (System/getProperty "telefunken.email")
                 :to to
                 :bcc bcc
                 :reply-to reply-to
                 :subject subject
                 :body [{:type "text/html" :content body}]
                 :message-id (if-let [domain (System/getProperty "telefunken.hostname")]
                               #(message-id domain)
                               #(message-id (str "postal." (.getHostName (InetAddress/getLocalHost)))))}))

(defn email-with-pdf [to subject body document]
  (send-message (config)
                {:from (System/getProperty "telefunken.email")
                 :to to
                 :bcc "bellybag@gmail.com"
                 :subject subject
                 :body [{:type "text/html"
                         :content body}
                        {:type :inline
                         :content (java.io.File. ^String document)
                         :content-type "application/pdf"}]}))
