When document plan is ready, we can export generated text using one of utility functions that are included in Accelerated Text project.

Go to `utils` folder located in project root, and run this command in the terminal:

```
clojure -A:generate \
    "Authorship" \
    "../api/test/resources/data-files/books.csv" \
    "output-eng.csv" \
    "Eng"
```

If all is well, you should see an output like this:

```
INFO  u.generate - Generating using 'Authorship' document plan for 'Eng' language
INFO  u.generate - Generating text for 5 data items
INFO  u.generate - Total 5 results to save
INFO  u.generate - Data The Business Blockchain has 4 variations
INFO  u.generate - Data The Age of Cryptocurrency has 4 variations
INFO  u.generate - Data Moving to the Cloud has 4 variations
INFO  u.generate - Data Building Search Applications has 4 variations
INFO  u.generate - Data Text Processing with GATE has 4 variations
```

Output file will contain original data as well as an additional `Variants` column at the end with text variants that were generated.
