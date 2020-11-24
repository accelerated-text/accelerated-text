# Purpose
Raw data can not always be presented in text as is. Numbers might need some formatting, names need cleaning or normalization. With Data Enrichment component a set of data transformation functions can be defined. They will modify the raw data before sending it to the text generation component. 

Let's say we have accounting data like this

 Account | CurrentPeriod (Q2) | PriorPeriod (Q1) 
---------|--------------------|-----------------
Gross Sales (ID1220) | 90447 | 82018 | 8429
Advertising (ID3011) | 1280 | 1982 | -702

When generating text we do not want ID part in the account name and we want the amounts in periods rounded to thousands using bite size formatting plus "USD" is needed at the end. We want something like this

 Account | CurrentPeriod (Q2) | PriorPeriod (Q1) 
---------|--------------------|-----------------
Gross Sales | around 90k USD | around 82k USD | $8429
Advertising | around 1k USD | around 2k USD | -$702

# Defining transformations

Accelerated Text stores data transformation rules in the `api/resources/config/enrich.edn` file. There might be separate transformation rules for different data types, this is controlled via `filenname-pattern` parameter. Which fields (columns) have to receive which changes is specified under `fields` parameter. Fields in turn is a collection of per field configurations. Configuration file structure is as follows:

* `file-pattern` - regular expression defining the file name for which this config will be active
* `fields` - a collection of field configurations
    * `name-pattern` - regex defining column name for this data type
    * `transformations` - a collection of functions performing transformations
        * `function` - any function which can transform the data (see bellow for the required parameter list for such function)
        * `args` - a map of arguments for the transformation function
        
Note that `-pattern` fields will take on regular expressions, but their patternless versions will be used for exact match. Thus `file-pattern` can be replace with `filename` and `name-pattern` with `name`.

# Configuration example

The following example configuration does the transformations outlined above. This has to be placed in `api/resources/config/enrich.edn` file for the transformations to take the effect.

```
[{:filename-pattern #regex"accounts.csv"
  :fields
  [{:name-pattern #"Account"
    :transformations
    [{:function :api.nlg.enrich.data.transformations/cleanup
      :args     {:regex#" \(.*?\)" :replacement ""}}]}
   {:name-pattern #regex".*Period .*"
    :transformations
    [{:function :api.nlg.enrich.data.transformations/number-approximation
      :args     {:scale      1000
                 :language   :en
                 :formatting :numberwords.domain/bites
                 :relation   :numberwords.domain/around}}
     {:function :api.nlg.enrich.data.transformations/add-symbol
      :args     {:symbol " USD" :position :back}}]}
   {:name-pattern #regex"Increase"
    :transformations
    [{:function :api.nlg.enrich.data.transformations/add-symbol
      :args     {:symbol "$" :position :front :skip #{\- \+}}}]}]}]
```

# Transformation functions

Any custom transformation function can be used as long as it conforms to this specification:

* its first parameter is the value from the data cell
* its second parameter is a map as specified in `args` configuration section
* it returns a modified cell value as string

Accelerated text provides a few transformation functions in its `api.nlg.enrich.data.transformations` namespace:

* `number-approximation` - Using [Number Words](https://github.com/tokenmill/numberwords) package turn a number to its numeric expression
* `add-symbol` - Add extra symbol to the front or the back of the value. Useful to add measurements or currency symbols
* `cleanup` - Cleanup the string using clojure.string/replace
