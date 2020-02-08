# Usage example:

```
curl -X POST http://localhost:8000 -d '{"text": "Located in city center", "context": {"city center": "{area}"}}'
```

context field tells enricher, that `city center` is actually a `{area}` placeholder (on which it is trained). By knowing this, it can generate correct text.