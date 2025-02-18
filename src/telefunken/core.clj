(ns telefunken.core
  (:require [postal.core :refer [send-message]]
            [postal.support :refer [message-id]]
            [clojure.java.io :as io])
  (:import java.net.InetAddress
           java.nio.file.Paths
           java.nio.file.Files
           java.net.URI))

(defn email? [s]
  (let [regex #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (boolean (re-matches regex s))))

(defn config [] {:host (System/getProperty "telefunken.smtp.host")
                 :user (System/getProperty "telefunken.smtp.user")
                 :pass (System/getProperty "telefunken.smtp.password")
                 :port (Integer. ^String (System/getProperty "telefunken.smtp.port"))})

(defn email
  "attachments is a vector of maps"
  [to subject body & {:keys [bcc reply-to attachments]}]
  (send-message (config)
                {:from (System/getProperty "telefunken.email")
                 :to to
                 :bcc bcc
                 :reply-to reply-to
                 :subject subject
                 :body (into [{:type "text/html; charset=utf-8" :content body}] attachments)
                 :message-id (if-let [domain (System/getProperty "telefunken.hostname")]
                               #(message-id domain)
                               #(message-id (str "postal." (.getHostName (InetAddress/getLocalHost)))))}))


(defn email-with-attachment [to subject body pathname & {:keys [type] :or {type :inline}}]
  {:pre [(some #{:inline :attachment} [type])]}
  (let [path (Paths/get (URI. (str "file://" ^String pathname)))
        mime-type (Files/probeContentType path)]
    (email to subject body :attachments (case type
                                          :inline [{:type :inline
                                                    :content (io/as-file ^String pathname)
                                                    :content-type mime-type}]
                                          :attachment [{:type :attachment
                                                        :content (io/as-file ^String pathname)}]))))

(def email-with-image email-with-attachment)
(def email-with-pdf email-with-attachment)
