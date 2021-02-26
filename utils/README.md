# About

Development utils to get and convert data

# Exporting

## DocumentPlan JSON -> Semantic Graph

```bash
clojure -M:dp print-graph <document-plan>.json
```

## Exporting document plans

Print single document plan:
```bash
clojure -M:dp print-plan [plan-name]
```

Save all documents to a default `../api/resources/document-plans` location:
```bash
clojure -M:dp export-plans
```

Or the output location can be specified as parameter:
```bash
clojure -M:dp export-plans my-plans
```
