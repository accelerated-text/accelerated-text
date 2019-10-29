(ns api.nlg.generator.amr-plan-parser-test
  (:require [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.nlg.generator.parser-ng :as parser-ng]
            [api.test-utils :refer [load-test-document-plan q]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each ddb-fixtures/wipe-ddb-tables)

(deftest ^:integration amr-plan-parser
         (let [query "mutation CreateDictionaryItem($name:String! $partOfSpeech:PartOfSpeech){createDictionaryItem(name:$name partOfSpeech:$partOfSpeech){name partOfSpeech}}"
               _ (q "/_graphql" :post {:query query :variables {:name "sees"}})
               query "mutation CreatePhrase($dictionaryItemId:ID! $text:String! $defaultUsage:DefaultUsage){createPhrase(dictionaryItemId:$dictionaryItemId text:$text defaultUsage:$defaultUsage){phrases{id text}}}"
               _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "sees"
                                                                :text             "sees"
                                                                :defaultUsage     "YES"}})
               _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "sees"
                                                                :text             "observe"
                                                                :defaultUsage     "YES"}})
               _ (q "/_graphql" :post {:query query :variables {:dictionaryItemId "sees"
                                                                :text             "watches"
                                                                :defaultUsage     "YES"}})
               [{quotes-dynamic :dynamic} {agent-dynamic :dynamic} {coagent-dynamic :dynamic}]
               (-> (load-test-document-plan "plain-amr")
                   (parser-ng/parse-document-plan nil nil)
                   first first)]
           (is (= [{:name  {:cell :actor :dyn-name "$2"}
                    :attrs {:amr true :title "Agent" :source :cell :type :cell}}]
                  agent-dynamic))
           (is (= [{:name {:cell :co-actor :dyn-name "$3"}
                    :attrs {:amr true :title "co-Agent" :source :cell :type :cell}}]
                  coagent-dynamic))
           (let [{:keys [attrs name]} (first quotes-dynamic)]
             (is (= {:source :quotes :type :amr} attrs))
             (is (= "$1" (:dyn-name name)))
             (is (= #{"$2 watches $3" "$2 observe $3" "$2 sees $3"} (set (map :value (:quotes name))))))))