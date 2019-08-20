(ns data-access.db.amr-rules)

(def see
  {:id "see",
   :thematic-roles
   (list {:type "Agent"}
         {:type "co-Agent"}),
   :frames
   (list
    {:examples (list "Harry sees Sally."),
     :syntax
     (list
      {:pos :NP, :value "Agent"}
      {:pos :VERB}
      {:pos :NP, :value "co-Agent"})})})

(def provide
  {:id "provide",
   :thematic-roles
   (list {:type "Agent"}
         {:type "co-Agent"}),
   :frames
   (list
    {:examples (list "Nike provides comfort."),
     :syntax
     (list
      {:pos :NP, :value "Agent"}
      {:pos :VERB}
      {:pos :NP, :value "co-Agent"})})})

(def rules
  {:provide provide
   :see     see})

(defn list-all [] (map (fn [[k v]] v) rules))
