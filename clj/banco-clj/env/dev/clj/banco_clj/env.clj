(ns banco-clj.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [banco-clj.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[banco-clj started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[banco-clj has shut down successfully]=-"))
   :middleware wrap-dev})
