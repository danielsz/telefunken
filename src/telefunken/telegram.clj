(ns telefunken.telegram
  (:require [clj-http.client :as client]
            [lang-utils.async :refer [with-async]]
            [clojure.string :as str]))

(def self-test #(client/get (str "https://api.telegram.org/bot" (System/getProperty "telegram.bot.token") "/getMe")
                            {:throw-exceptions false
                             :as :auto}))

(def get-updates #(client/get (str "https://api.telegram.org/bot" (System/getProperty "telegram.bot.token") "/getUpdates")
                              {:throw-exceptions false
                               :as :auto}))

(defn send-message
  ([text]
   (send-message text {:parse-mode ""}))
  ([text {:keys [parse-mode]}]
   {:pre [(some #{parse-mode} ["MarkdownV2" "Markdown" "HTML" ""])]}
   (client/post (str "https://api.telegram.org/bot" (System/getProperty "telegram.bot.token") "/sendMessage")
               {:throw-exceptions false
                :form-params {:chat_id (Integer. ^String (System/getProperty "telegram.chat.id"))
                              :text text
                              :parse_mode parse-mode}
                :as :json})))

(defn async-send-message [& text]
  (with-async (send-message (str/join " " text))))

(defn async-send-html [& text]
  (with-async (send-message (str/join " " text) {:parse-mode "HTML"})))

(defn async-send-markdown [& text]
  (with-async (send-message (str/join " " text) {:parse-mode "MarkdownV2"})))

(defn pulse [interval] (async-send-message (str "*Pulse* _" interval "_")))
