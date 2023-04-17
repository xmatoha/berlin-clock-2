(ns berlin-clock-2.api)

(defn berlin-clock-handler []
  (fn [{{{:keys [time]} :query} :parameters}]
    {:status 200 :body {}}))

