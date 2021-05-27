(ns banco-clj.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[banco-clj started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[banco-clj has shut down successfully]=-"))
   :middleware identity})
