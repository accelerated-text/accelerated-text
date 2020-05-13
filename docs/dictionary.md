Dictionary is a collection of dictionary item blocks that can be used from Document plan editor.

At the moment, there is no way to create dictionary items through user interface, so we must rely on configuration files.

By default, dictionary files are kept in `api/resources/dictionary` and use [EDN](https://github.com/edn-format/edn) format.

We begin with creating a new .edn file, for example `eng.edn`. This is the file that will contain all entries that we will define in this chapter, but there may be any number of dictionary files.

This is how a single dictionary item looks like:

```clojure
{:key        "write"
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
[{:key        "write"
  :category   "V"
  :language   "Eng"
  :forms      ["write" "wrote" "written"]}
 {:key        "author"
  :category   "V"
  :language   "Eng"
  :forms      ["author"]}
 {:key        "author"
  :category   "N"
  :language   "Eng"
  :forms      ["author"]}]
```

Let's create another file for German language - `ger.edn` - which will be of use later on.

```clojure
[{:key        "write"
  :category   "V"
  :language   "Ger"
  :forms      ["schreiben" "schreibt" "schrieb" "schriebe" "geschrieben"]}
 {:key        "author"
  :category   "V"
  :language   "Ger"
  :forms      ["verfassen"]}
 {:key        "author"
  :category   "N"
  :language   "Eng"
  :forms      ["autor"]
  :attributes {"Gender" "masculine"}}]
```

Since keys are the same for both languages, Accelerated Text will use the correct forms when needed. Also notice that there is an additional `Gender` attribute for German nouns. More information on how forms should look like and what additional attributes need to be defined can be found [here](https://www.grammaticalframework.org/lib/doc/synopsis/).

