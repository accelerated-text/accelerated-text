# Generated text export

When document plan is ready, we can export generated text using one of utility functions that are included in Accelerated Text project.

To use these utils, [Clojure programming language](https://www.clojure.org/guides/getting_started) must be installed on the system.

Alternatively, you can use our [Python wrapper for Accelerated Text](py-wrapper.md), which is especially recommended on Windows, since installing Clojure on this operating system may be tricky.

After Clojure is installed, go to `utils` folder located in project root, and run this command in the terminal:

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

# Document plan export

To be able to share document plans or save them as a backup, go to `utils` folder located in project root and run this command in the terminal:
```
make export-all-document-plans
```

This should produce output similar to this:
```
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/uTbZELWTIRnxCfmWLGfq.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/AHOPQhdyQdcRpEngZjLk.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/FtrMQwvHjYCGwNJqtuWT.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/aRBRdMShfBCpwJRERXUZ.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/yQqvEjwWbHlZVXxJtqYE.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/amr/vNfPkkdMRhOpItYo.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/oITyUhfqXtrYcwtWBcrz.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dp/fFOhndCHismNBnal.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/hfTnGajQTQkAQMHMzjPk.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dlg/UYvoFHjkRcqynZROytYV.json
INFO  u.document-plan - Writing: ../api/resources/document-plans/dp/dotafNstSKQPbfNt.json
```

The document plans will be saved in `api/resources/document-plans` by default. This can be changed by providing `dir` argument like this:
```
make export-all-document-plans dir=my-output-dir
```

Whenever document plans are exported and kept in the default directory (`api/resources/document-plans`), they will be initialized every time Accelerated Text is run.
