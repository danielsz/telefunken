(ns telefunken.newsletter
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [cljs-time.core :as t]
   [cljs-time.coerce :as c]
   [cljs-time.format :refer [formatters unparse parse]]
   [cljs.core.match :refer-macros [match]]
   [om-flash-bootstrap.core :as f]))

(defn parse-date [date]
  (let [formatter (formatters :date)]
    (parse formatter date)))

(defn unsubscribe
  "Om component for new unsubscribe"
  [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:button-status false})
    om/IWillMount
    (will-mount [_]
      (let [chsk-send! (om/get-state owner :chsk-send!)]
        (chsk-send! [:telefunken/api {:unsubscribe-details :newsletter}] 8000
                    (fn [details]
                      (om/set-state! owner :unsubscribe-details details)))))
    om/IDisplayName
    (display-name [this]
      "unsubscribe")
    om/IRender
    (render [_]
      (let [flash (om/get-state owner :flash)
            chsk-send! (om/get-state owner :chsk-send!)
            email (:email (om/get-state owner :unsubscribe-details))
            expiration (:expiration (om/get-state owner :unsubscribe-details))
            valid? (fn [expiration]
                     (t/within? (t/interval (t/now) (t/plus (t/now) (t/months 1))) (parse-date expiration)))]
        (when (some? email) (om/set-state! owner :button-status false))
        (dom/div #js {:className "row"}
                 (dom/div #js {:className "col-md-6 col-md-offset-3"}
                          (dom/div #js {:className "well"}
                                   (dom/h3 nil "Unsubscribe")
                                   (dom/p nil "You are unsubscribing from our newsletter.")
                                   (dom/p nil (str "Email: " email))
                                   (dom/button #js {:className "btn btn-default"
                                                    :type "submit"
                                                    :disabled (om/get-state owner :button-status)
                                                    :onClick (fn [_]
                                                               (if (not (valid? expiration))
                                                                 (f/warn flash "That link has expired")
                                                                 (chsk-send! [:telefunken/api {:unsubscribe-from-newsletter email}] 8000
                                                                             (fn [cb-reply]
                                                                               (match [cb-reply]
                                                                                      [{:success message}] (f/bless flash message)
                                                                                      [{:info message}] (f/info flash message)
                                                                                      [{:error message}] (f/warn flash message)
                                                                                      [:chsk/timeout] (f/warn flash "The request timed out.")
                                                                                      :else (println "no match found")))))
                                                               (om/set-state! owner :button-status true))}
                                               "Confirm"))))))))
