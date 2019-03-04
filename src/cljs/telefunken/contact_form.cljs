(ns telefunken.contact-form
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-utils.core :refer [form-data]]
            [om-flash-bootstrap.core :as f]
            [goog.net.XhrIo :as xhr]))

(defn contact
  "Om component for new contact"
  [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:submit-status false
       :subject ""
       :body ""})
    om/IDisplayName
    (display-name [this]
      "contact")
    om/IRender
    (render [_]
      (let [flash (om/get-state owner :flash)
            from-fn (om/get-state owner :from-fn)]
        (dom/div #js {:className "content"}
                 (dom/section #js {:id "contact-section"}
                              (dom/h3 nil "Contact form")
                              (dom/form #js {:id "contact-form"
                                             :className "ui-elem"
                                             :onInvalid (fn [e]
                                                          (f/warn flash (.-validationMessage (.-target e))))
                                             :onSubmit (fn [e]
                                                         (.preventDefault e)
                                                         (xhr/send "/contact" (fn [e]
                                                                                (f/bless flash (str "Mail succesfully sent from " (from-fn @data)))
                                                                                (om/set-state! owner :subject "")
                                                                                (om/set-state! owner :body "")
                                                                                (om/set-state! owner :submit-status true))
                                                                   "POST"
                                                                   (form-data {:from (from-fn @data)
                                                                               :subject (om/get-state owner :subject)
                                                                               :body (om/get-state owner :body)})
                                                                   #js {"X-CSRF-Token" js/antiForgeryToken}))}
                                        (dom/div #js {:className "form-group"}
                                                 (dom/label #js {:htmlFor "subject"} "Subject")
                                                 (dom/input #js {:className "form-control"
                                                                 :required "required"
                                                                 :type "text"
                                                                 :placeholder "Please type the subject of your inquiry"
                                                                 :onChange (fn [e] (let [val (.-value (.-target e))]
                                                                                    (om/set-state! owner :subject val)))
                                                                 :value (om/get-state owner :subject)}))
                                        (dom/div #js {:className "form-group"}
                                                 (dom/label #js {:htmlFor "body"} "Body")
                                                 (dom/textarea #js {:className "form-control"
                                                                    :required "required"
                                                                    :rows "5"
                                                                    :placeholder "Please type the content of your inquiry"
                                                                    :onChange (fn [e] (let [val (.-value (.-target e))]
                                                                                       (om/set-state! owner :body val)))
                                                                    :value (om/get-state owner :body)}))
                                        (dom/input #js {:type "submit"
                                                        :name "submit"
                                                        :value "Send"
                                                        :disabled (om/get-state owner :submit-status)}))))))))
