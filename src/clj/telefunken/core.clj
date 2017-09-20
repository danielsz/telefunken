(ns telefunken.core
  (:require [postal.core :refer [send-message]]
            [postal.support :refer [message-id]]
            [environ.core :refer [env]])
  (:import java.net.InetAddress))

(defn email? [s]
  (let [regex #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (boolean (re-matches regex s))))

(defn config [] {:host (:telefunken-smtp-host env)
                 :user (:telefunken-smtp-user env)
                 :pass (:telefunken-smtp-password env)
                 :port (Integer. ^String (:telefunken-smtp-port env))})

(defn email [to subject body & {:keys [bcc]}] 
  (send-message (config)
                {:from (:telefunken-email env)
                 :to to
                 :bcc bcc
                 :subject subject
                 :body [{:type "text/html" :content body}]
                 :message-id (if-let [domain (:telefunken-hostname env)]
                               #(message-id domain)
                               #(message-id (str "postal." (.getHostName (InetAddress/getLocalHost)))))}))

(defn email-from [from subject body & {:keys [type to] :or {type "text/plain"
                                                            to (:telefunken-email env)}}]
  (send-message (config)
                {:from from
                 :to to
                 :subject subject
                 :body [{:type type :content body}]}))

(defn email-with-pdf [to subject body document]
  (send-message (config)
                {:from (:telefunken-email env)
                 :to to
                 :bcc "bellybag@gmail.com"
                 :subject subject
                 :body [{:type "text/html"
                         :content body}
                        {:type :inline
                         :content (java.io.File. ^String document)
                         :content-type "application/pdf"}]}))
