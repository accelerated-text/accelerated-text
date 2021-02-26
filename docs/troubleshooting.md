# Troubleshooting

## db.error/not-an-entity

The error message looks like this:

```
Caused by: java.lang.IllegalArgumentException: :db.error/not-an-entity Unable to resolve entity: :document-plan/data-sample-method
```

This means that new version of Accelerated Text was run which included changes to database schema.

There are several ways this can be fixed. If you don't need to recover existing document plans, just run `make delete-datomic-volume` command and restart Accelerated Text, otherwise:

1. Revert to previous version by editing `docker-compose.yml` `acc-text-api` image tag, for example, by changing `acctext/api:latest` to `acctext/api:1541ab8fbba68a86d01c085d7a6e2577db75b1f0` (all available tags can be found [here](https://hub.docker.com/r/acctext/api)).
2. [Export document plans](export.md)
3. run `make delete-datomic-volume`
4. Restore `acc-text-api` image tag to `latest`
