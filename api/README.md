# About

This module provides API access to the text generation functionality defined in Accelerated Text `core` module. 
It also ensures connectivity to all the data bases which provide data for the text generation. This way `core`
is completely separated from the data storage mechanics.

## Running locally

Start one of the database servers we support [*Datomic*](https://docs.datomic.com/on-prem/get-datomic.html) or 
[*DynamoDB*](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html). 
Once it is running start API server with the following command

- `make run-local-server`

After execution API will be reachable via `http://localhost:8080/`

# graphql-api

NLG API backend as a GraphQL.

GraphQL implementation is based on [Lacinia](https://github.com/walmartlabs/lacinia).

Schema definition is in the `resources/schema.edn` file.

GraphQL implementation is in `src/api/graphql/core.clj`.
