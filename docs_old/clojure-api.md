## Clojure API use

Accelerated Text UI helps with creating document plans and testing them with sample data. 
Accelerated Text's generation functionality can be used directly from the Clojure code.

Lets say you have a book data limited to the author and the book title:

| title                       | authors       |
| -----                       | ------        |
| Frankenstein                | M. W. Shelley |
| Dracula                     | Bram Stoker   |
| The Island of Doctor Moreau | H.G. Wells    |

When working via UI this data needs to be uploaded as the CSV. To use it in the code we'll have to represent it as a Clojure map. 

```clojure
(def data
  [{:title   "Frankenstein"
    :authors "M. W. Shelley"}
   {:title   "Dracula"
    :authors "Bram Stoker"}
   {:title   "The Island of Doctor Moreau"
    :authors "H.G. Wells"}])
```

Second component needed for generation is the plan itself. In UI it has a nice representation in visual blocks, and is persisted in the structure like this:

```clojure
(def document-plan
  {:type     "Document-plan"
   :segments [{:type     "Segment"
               :children [{:type           "AMR"
                           :conceptId      "author"
                           :dictionaryItem {:type   "Dictionary-item"
                                            :itemId "author"
                                            :name   "author"}
                           :roles          [{:name     "agent"
                                             :children [{:name "authors"
                                                         :type "Cell"}]}
                                            {:name     "co-agent"
                                             :children [{:name "title"
                                                         :type "Cell"}]}
                                            {:name "theme" :children [nil]}]}]}]})
```
