# Natural Language Generation for Accelerated Text

Library which allows to generate text using CCG defined grammar

## Usage

Inside `ccg-kit.grammar` there is a function:

```clojure
(defn generate
  "Generates multiple of valid sentences from given array of tokens"
  [^Grammar grammar & tokens]
  (...))
```

Given grammar object and array of tokens, it returns text

## Compiling Grammar

In order to generate something, you need to have grammar

### Manually building

Example can be found at `test/predefined.clj`

Using DSL, families can be constructed, eg.:

```clojure
(dsl/family "Product" :NNP false
              (dsl/entry
               "Primary"
               (dsl/lf "P:shoe")
               (dsl/atomcat :NNP {:index 2}
                            (dsl/fs-nomvar "index" "P")
                            (dsl/fs-featvar "design" "DESIGN:garment"))))
```

Largest caveat - you need to know how CCG works in order to know how to build grammar

### From AMR

An AMR (Abstract Meaning Representation) structure can be compiled into Grammar (usually multiple)

Example:
```clojure
(def author-amr
  {:id "author"
   :members [{:name "written"}]
   :thematic-roles
   (list {:type "Agent"}
         {:type "co-Agent"})
   :frames
   (list
    {:examples (list "X is the author of Y")
     :syntax
     (list
      {:pos :NP :value "Agent"}
      {:pos :LEX :value "is"}
      {:pos :LEX :value "the author of"}
      {:pos :NP :value "co-Agent"})}
    {:examples (list "Y is written by X")
     :syntax
     (list
      {:pos :NP :value "co-Agent"}
      {:pos :LEX :value "is"}
      {:pos :VERB}
      {:pos :PREP :value "by"}
      {:pos :NP :value "Agent"})})})
```

Using `[ccg-kit.verbnet.ccg :as ccg]`

can be compiled like this:

```
(ccg/vn->grammar author-amr)
```


This test illiustrates it:

```clojure
(deftest complex-amr-to-text
  (let [grammars (vn->grammar author-amr)]
       (testing "First frame"
         (let [results (set (grammar/generate (first grammars) "{{AGENT}}" "{{CO-AGENT}}"))]
           (log/debugf "Results: %s" (pr-str results))
           (is (contains? results "{{AGENT}} is the author of {{CO-AGENT}}"))))
       (testing "Second frame"
         (let [results (set (grammar/generate (second grammars) "{{AGENT}}" "{{CO-AGENT}}" "written"))]
           (log/debugf "Results: %s" (pr-str results))
           (is (contains? results "{{CO-AGENT}} is written by {{AGENT}}"))))))
```
