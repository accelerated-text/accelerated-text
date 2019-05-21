(ns nlg.lexicon
  (:require [db.dynamo-ops :as ops]
            [nlg.utils :as utils]))

(defn get-db [] (ops/db-access :lexicon))

(defn get-key-id [key]
  (-> (re-find #"^.+\.(\d+)$" key)
      (second)
      (Integer/parseInt)))

(defn get-used-key-ids [m]
  (set (map #(get-key-id (get % :key)) m)))

(defn next-keys [db word]
  (let [matches (ops/scan! db {:attr-conds {:word [:eq word]}})
        ids (-> (get-used-key-ids matches) (conj 0))]
    (map (partial format (str word ".%s"))
         (remove #(contains? ids %) (range)))))

(defn sort-entries [coll]
  (sort-by (fn [{:keys [word key]}] [word (get-key-id key)]) coll))

(defn remove-word-key [coll]
  (map #(dissoc % :word) coll))

(defn create-single [db key request-body]
  (let [word (first (get request-body :synonyms))]
    (utils/do-update (when (< 0 (count word))
                       (comp #(dissoc % :word)
                             (partial ops/write! db key))) (-> request-body
                                                               (assoc :word word)
                                                               (dissoc :key)))))

(defn create-multiple [db request-body]
  (let [request-map (group-by #(first (get % :synonyms)) request-body)
        words (keys request-map)
        key-map (zipmap words (map (partial next-keys db) words))]
    (flatten (->> words
                  (pmap (fn [word]
                          (mapv (partial create-single db)
                                (get key-map word)
                                (get request-map word))))))))

(defn create [request-body]
  (let [db (get-db)]
    (if (map? request-body)
      (let [word (first (get request-body :synonyms))
            key (first (next-keys db word))]
        (create-single db key request-body))
      (utils/add-status (create-multiple db request-body)))))

(defn modify [path-params request-body]
  (let [db (get-db)
        key (get path-params :id)
        word (first (get request-body :synonyms))
        response (utils/do-return ops/read! db key)]
    (if (not= 200 (get response :status))
      response
      (utils/do-update (comp #(dissoc % :word) (partial ops/update! db))
                       key
                       (merge
                         (get response :body)
                         (dissoc request-body :key)
                         (when word
                           {:word word}))))))

(defn delete [path-params]
  (let [db (get-db)
        id (get path-params :id)]
    (utils/do-delete (comp #(dissoc % :word) (partial ops/read! db))
                     (partial ops/delete! db) id)))

(defn process-search-response [resp offset limit]
  (let [count (count resp)]
    {:offset     offset
     :totalCount count
     :limit      limit
     :items      (-> resp
                     (sort-entries)
                     (remove-word-key)
                     (vec)
                     (subvec (min count offset)
                             (min count (+ offset limit))))}))

(defn list-entries [db offset limit]
  (utils/do-return (comp #(process-search-response % offset limit) (partial ops/list! db)) nil))

(defn scan [db query offset limit]
  (let [length (count query)]
    (utils/do-return
      (comp #(process-search-response % offset limit)
            (partial ops/scan! db))
      {:attr-conds {:word (cond
                            (= \* (first query) (last query)) [:contains (subs query 1 (dec length))]
                            (= \* (first query)) [:contains (subs query 1 length)]
                            (= \* (last query)) [:begins-with (subs query 0 (dec length))]
                            :else [:eq query])}})))

(defn search [query-params _]
  (let [db (get-db)
        query (get query-params :query)
        offset (max 0 (Integer/parseInt (get query-params :offset "0")))
        limit (max 0 (Integer/parseInt (get query-params :limit "20")))]
    (if (or (nil? query) (= "*" query))
      (list-entries db offset limit)
      (scan db query offset limit))))
