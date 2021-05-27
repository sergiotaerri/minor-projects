(ns banco-clj.spec
  (:require
   [banco-clj.config :refer [env]]
   [clojure.spec.alpha :as s]
   [spec-tools.spec :as spec]
   [banco-clj.db.core :as db]))

(s/def ::id spec/int?)
(s/def ::cliente_id spec/int?)
(s/def ::tipo spec/int?)
(s/def ::credito (s/or :int spec/int? :float spec/float?))
(s/def ::created_at (s/or :null spec/nil? :inst inst? :some some?))
(s/def ::deleted_at (s/or :null spec/nil? :inst inst? :some some?))

(s/def ::conta (s/keys :req-un [::credito ::cliente_id]
                       :opt-un [::id ::created_at ::deleted_at ::tipo]))

(s/def ::nome spec/string?)

(s/def ::cliente (s/keys :req-un [::nome]
                         :opt-un [::id ::created_at ::deleted_at]))
