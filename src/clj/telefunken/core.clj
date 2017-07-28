(ns telefunken.core
  (:require [postal.core :refer [send-message]]
            [postal.support :refer [message-id]]
            [environ.core :refer [env]]))

;(System/setProperty "postal.version" "1.11.3")

(defn email? [s]
  (let [regex #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (boolean (re-matches regex s))))

(defn config [] {:host (:telefunken-smtp-host env)
                 :user (:telefunken-smtp-user env)
                 :pass (:telefunken-smtp-password env)
                 :port (Integer. (:telefunken-smtp-port env))})

(defn email [to subject body & {:keys [bcc]}] 
  (send-message (config)
                {:from (:telefunken-email env)
                 :to to
                 :bcc "bellybag@gmail.com" ;should be bcc
                 :subject subject
                 :body [{:type "text/html" :content body}]
                 :message-id #(message-id "mg.twitter-fu.com")}))

(defn email-from [from subject body & {:keys [type] :or {type "text/plain"}}]
  (send-message (config)
                {:from from
                 :to (:telefunken-email env)
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
                         :content (java.io.File. document)
                         :content-type "application/pdf"}]}))


