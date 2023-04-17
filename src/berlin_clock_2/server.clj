(ns berlin-clock-2.server
  (:require
   [muuntaja.interceptor]
   [reitit.http :as http]
   [reitit.interceptor.sieppari :as sieppari]
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.coercion.schema]
   [reitit.http.interceptors.exception :as exception]
   [reitit.coercion.spec]
   [reitit.http.coercion :as coercion]
   [reitit.coercion.malli]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [reitit.http.interceptors.parameters :as parameters]
   [ring.adapter.jetty :as jetty]
   [reitit.dev.pretty :as pretty]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [berlin-clock-2.api :refer [berlin-clock-handler]]))

(defonce server-handle (atom {}))

(defn routes []
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "tic tac toe game api "
                            :description "tic tac toe game api"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/berlin-clock"
    {:get {:summary "get berlin clock representation"
           :responses
           {200
            {}}
           :coercion reitit.coercion.malli/coercion
;           :parameters {:query [:time string?]}
           :handler berlin-clock-handler}}]])

(defn app-with-deps [routes]
  (http/ring-handler
   (http/router
    routes
    {:exception pretty/exception
     :data {:coercion reitit.coercion.malli/coercion
            :muuntaja m/instance
            :interceptors [swagger/swagger-feature
                           (parameters/parameters-interceptor)
                           (muuntaja/format-negotiate-interceptor)
                           (muuntaja/format-response-interceptor)
                           (exception/exception-interceptor)
                           (muuntaja/format-request-interceptor)
                           (coercion/coerce-response-interceptor)
                           (coercion/coerce-request-interceptor)]}})

   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/api"
      :config {:validatorUrl nil
               :operationsSorter "alpha"}})
    (ring/create-default-handler))
   {:executor sieppari/executor}))

(defn server-start [options routes]
  (println (str "Starting server on port " (get options "PORT")))
  (reset! server-handle (jetty/run-jetty (app-with-deps routes) {:host "localhost" :port (Integer/parseInt (get options "PORT")), :join? false, :async true})))

(defn server-stop []
  (println "Stopping server ...")
  (.stop @server-handle))

(defn restart-server []
  (when @server-handle (server-stop))
  (server-start {"PORT" "3000"}
                (routes)))

(comment (restart-server))
(comment (server-stop))

