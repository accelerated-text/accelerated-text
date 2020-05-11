Dictionary is a collection of dictionary item blocks that can be used from Document plan editor.

At the moment, there is no way to create dictionary items through user interface, so we must rely on configuration files.

By default, dictionary files are kept in `api/resources/dictionary` and use [EDN](https://github.com/edn-format/edn) format.

We begin with creating a new .edn file, for example `eng.edn`. This is the file that will contain all entries that we will define in this chapter, but there may be any number of dictionary files.

This is how a single dictionary item looks like:

```clojure
#:acc-text.nlg.dictionary.item{:key        "write_V"
                               :category   "V"
                               :language   "Eng"
                               :forms      ["write" "wrote" "written"]}
```

Let's go through each of the dictionary item attributes:

* **Key** is the visible name for a dictionary item, and it is reused across different languages
* **Category** determines whether this is a verb (V), a noun (N), an adjective (A), or an adverb (Adv)
* **Language** code for this entry, uses capitalised three letter language code: (Eng)lish, (Ger)man, (Rus)sian
* **Forms** that will vary depending on tense, most of the time single form will be enough 
* **Attributes** *(optional)* define additional information, for example, gender

Dictionary file may look like this:

```clojure
[#:acc-text.nlg.dictionary.item{:key        "write_V"
                                :category   "V"
                                :language   "Eng"
                                :forms      ["write" "wrote" "written"]}
 #:acc-text.nlg.dictionary.item{:key        "author_V"
                                :category   "V"
                                :language   "Eng"
                                :forms      ["author"]}
 #:acc-text.nlg.dictionary.item{:key        "author_N"
                                :category   "N"
                                :language   "Eng"
                                :forms      ["author"]}]
```







