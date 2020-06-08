# Natural Language Generation Core

This is the main module of Accelerated Text.

In order to generate text, Core uses *Semantic Graphs* - structures that describe what the text should look like when generation is complete.

One *Semantic Graph* is enough to generate many text variations for multiple languages.

## Prerequisites

At the lowest level, Accelerated Text Core uses [Grammatical Framework](https://www.grammaticalframework.org/).

To launch *GF* service, enter:

```
make run-gf-service
```

## Development

Main entrypoint for text generation can be accessed by invoking *acc-text.nlg.core/generate-text*.

We can also visualize semantic graphs using [GraphViz](https://graphviz.org/). 

```clojure
(def semantic-graph
  #:acc-text.nlg.semantic-graph
      {:relations [{:from :01 :to :02 :role :segment}
                   {:from :02 :to :03 :role :instance}
                   {:from :03 :to :04 :role :child}
                   {:from :03 :to :05 :role :modifier}
                   {:from :05 :to :06 :role :child}
                   {:from :05 :to :09 :role :modifier}
                   {:from :06 :to :07 :role :item}
                   {:from :06 :to :08 :role :item}]
       :concepts  [{:id :01 :type :document-plan}
                   {:id :02 :type :segment}
                   {:id :03 :type :modifier}
                   {:id :04 :type :dictionary-item :name "here" :category "Adv"}
                   {:id :05 :type :modifier}
                   {:id :06 :type :synonyms}
                   {:id :07 :type :dictionary-item :name "venue" :category "N"}
                   {:id :08 :type :dictionary-item :name "restaurant" :category "N"}
                   {:id :09 :type :dictionary-item :name "affordable" :category "A"}]})
(acc-text.nlg.semantic-graph.utils/vizgraph semantic-graph)
```

<img src="resources/docs/graph.png" width="1000"/>

Context includes data, dictionary and AMRs that provide rules how these concepts should be realized in semantic graph.

Since our Semantic Graph includes only dictionary items, only they need to be defined in context.

```clojure
(def context
  {:dictionary  [#:acc-text.nlg.dictionary.item{:key "venue" 
                                                :category "N" 
                                                :language "Eng" 
                                                :forms ["venue"]}
                 #:acc-text.nlg.dictionary.item{:key "restaurant" 
                                                :category "N"
                                                :language "Eng" 
                                                :forms ["restaurant"]}
                 #:acc-text.nlg.dictionary.item{:key "affordable" 
                                                :category "A" 
                                                :language "Eng" 
                                                :forms ["affordable"]}
                 #:acc-text.nlg.dictionary.item{:key "here" 
                                                :category "Adv" 
                                                :language "Eng" 
                                                :forms ["here"]}]})
```

Finally, let's generate text for this semantic graph.
```clojure
(acc-text.nlg.core/generate-text semantic-graph context "Eng")
=>
({:text "There is affordable venue here."
  :language "Eng"
  :tokens [{:text "There" :idx 0}
           {:text "is" :idx 6}
           {:text "affordable" :idx 9}
           {:text "venue" :idx 20}
           {:text "here" :idx 26}
           {:text "." :idx 30}]}
 {:text "There is affordable restaurant here."
  :language "Eng"
  :tokens [{:text "There" :idx 0}
           {:text "is" :idx 6}
           {:text "affordable" :idx 9}
           {:text "restaurant" :idx 20}
           {:text "here" :idx 31}
           {:text "." :idx 35}]})
```
