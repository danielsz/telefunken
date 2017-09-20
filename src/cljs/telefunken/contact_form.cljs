(ns telefunken.contact-form
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-flash-bootstrap.core :as f]
            [ajax.core :refer [POST]]))

(defn contact
  "Om component for new contact"
  [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:submit-status false})
    om/IDisplayName
    (display-name [this]
      "contact")
    om/IRender
    (render [_]
      (let [flash (om/get-state owner :flash)
            chsk-send! (om/get-state owner :chsk-send!)
            logged-in? (om/get-state owner :logged-in?)
            chsk-state (om/get-state owner :chsk-state)
            from-fn (om/get-state owner :from-fn)]
        (dom/div #js {:className "row"}
                 (dom/div #js {:className "col-md-6 col-md-offset-3"}
                          (dom/div #js {:className "well"}
                                   (dom/h3 nil "Email")
                                   (dom/form nil
                                             (dom/div #js {:className "form-group"}
                                                      (dom/label #js {:htmlFor "subject"} "Subject")
                                                      (dom/input #js {:className "form-control"
                                                                      :id "subject"
                                                                      :ref "subject"
                                                                      :required "required"
                                                                      :disabled (not (logged-in?))
                                                                      :type "text"
                                                                      :placeholder "Please type the subject of your inquiry"}))
                                             (dom/div #js {:className "form-group"}
                                                      (dom/label #js {:htmlFor "body"} "Body")
                                                      (dom/textarea #js {:className "form-control"
                                                                         :id "body"
                                                                         :ref "body"
                                                                         :required "required"
                                                                         :disabled (not (logged-in?))
                                                                         :rows "5"
                                                                         :placeholder "Please type the content of your inquiry"}))
                                             (dom/button #js {:className "btn btn-default"
                                                              :type "submit"
                                                              :disabled (om/get-state owner :button-status)
                                                              :onClick (fn [e]
                                                                         (.preventDefault e)
                                                                         (if-not (logged-in?) (f/warn flash "Please sign in first")
                                                                                 (if (.checkValidity (om/get-node owner "body"))
                                                                                   (POST "/contact" {:headers {"X-CSRF-Token" (:csrf-token @chsk-state)}
                                                                                                     :params {:from (from-fn @data)
                                                                                                              :subject (.-value (om/get-node owner "subject"))
                                                                                                              :body (.-value (om/get-node owner "body"))}
                                                                                                     :handler (fn [response]
                                                                                                                (f/bless flash (str "Mail succesfully sent from " response))
                                                                                                                (set! (.-value (om/get-node owner "body")) "")
                                                                                                                (set! (.-value (om/get-node owner "subject")) "")
                                                                                                                (om/set-state! owner :button-status true))
                                                                                                     :error-handler (fn [status status-test] (f/bless flash "Succesfully published"))})
                                                                                   (f/warn flash (.-validationMessage (om/get-node owner "body"))))))} "Submit"))
                                   (dom/p nil (when-not (logged-in?) "Please sign in before typing your message.")))))))))
