(ns telefunken.telegram
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]
            [lang-utils.async :refer [with-async]]
            [clojure.string :as str]))

(def self-test #(client/get (str "https://api.telegram.org/bot" (:telegram-bot-token env) "/getMe")
                            {:throw-exceptions false
                             :as :auto}))

(def get-updates #(client/get (str "https://api.telegram.org/bot" (:telegram-bot-token env) "/getUpdates")
                              {:throw-exceptions false
                               :as :auto}))

(defn send-message [text]
  (client/post (str "https://api.telegram.org/bot" (:telegram-bot-token env) "/sendMessage")
               {:throw-exceptions false
                :form-params {:chat_id (Integer. ^String (:telegram-chat-id env))
                              :text text
                              :parse_mode "Markdown"}
                :as :json}))

(defn async-send-message [& text]
  (with-async (send-message (str/join " " text))))

(defn pulse [interval] (async-send-message (str "*Pulse* _" interval "_")))
