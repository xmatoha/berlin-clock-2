(ns steps-definitions.core-steps
  (:require [lambdaisland.cucumber.dsl :refer :all]
            [clojure.test :refer :all]
            [berlin-clock-2.core :refer :all]
            [clj-http.client :as http-client]))

(Given "the API endpoint /berlin-clock" [state]
       state)

(When "I request the time for 00:00:00" [state]
      (assoc state :get-result (http-client/get "http://localhost:8080" {:accept :json :query-params {"time", "00:00:00"}})))

(Then "the seconds are Y" [state]
      (is (get-in  [:get-result :state] state) 200)
      state)


