# About

Development utils to get and convert data

# Exporting

## DocumentPlan JSON -> Semantic Graph

```bash
clojure -M:export semantic-graph <document-plan>.json
```

## Exporting document plans

Print single document plan:
```bash
clojure -M:export plan [plan-name]
```

Save all documents to a default `../api/resources/document-plans` location:
```bash
clojure -M:export all-plans
```
