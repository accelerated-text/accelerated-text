Here we will describe Accelerated Text GraphQL API usage in order to fetch generated texts. For this purpose we will need to pass two bits of information to the NLG backend: *document plan identifier* and *data item identifier*.

The GraphQL endpoint is accessible at `http://localhost:3001/_graphql`. CURL will be used to illustrate the calls to the back end.

First lets get registered document plans:

```
curl -X POST -H "Content-Type: application/json" \
  --data '{"query": "{documentPlans(offset:0 limit:10){totalCount items{id name dataSampleId dataSampleRow}}}" }' \
  http://localhost:3001/_graphql
```

This will return a list of document plans:

```json
{
  "data": {
    "documentPlans": {
      "totalCount": 1,
      "items": [
        {
          "id": "e39836ed-0283-4080-a436-554d9c48b839",
          "name": "Book store",
          "dataSampleId": "ba177b94-1963-4e21-979d-bf0cba75e05c",
          "dataSampleRow": 0
        }
      ]
    }
  }
}
```

The `id` field gives document plan id, and the `dataSampleId` field specifies which data to use. Using these fields we construct a text generation request.
With this, a second call has to be made to get the results identifier for actual sentence polling. Polling is used because text is not generated right away, NLG process for a more complcated plans can take some time.

```
curl -XPOST -H "Content-Type: application/json" \
  --data '{"documentPlanId":"e39836ed-0283-4080-a436-554d9c48b839","dataId":"ba177b94-1963-4e21-979d-bf0cba75e05c"}' \
  http://localhost:3001/nlg/ 
```

A result id is returned:

```json
{"resultId": "6f26099d-429d-41e9-9800-83ab58c59ddd"}
```

With this a final request can be made to fetch the results. Note that it can be done repeatedly with high performance, since the text generation is not happening at this stage.

```
curl -XGET -H "Content-Type: application/json" http://localhost:3001/nlg/6f26099d-429d-41e9-9800-83ab58c59ddd
```

You should get generated text with annotations (data is truncated):

```json
{
  "offset": 0,
  "totalCount": 2,
  "ready": true,
  "variants": [
    {
      "type": "ANNOTATED_TEXT",
      "id": "ae9a1d60-4aa6-49da-9738-480243a5095b",
      "children": [
        {
          "type": "PARAGRAPH",
          "id": "ab8b650a-8774-4992-8a56-fe8d01f74097",
          "children": [
            {
              "type": "SENTENCE",
              "id": "5cda3e9f-8fad-4b69-a0a3-f9f5e9a19465",
              "children": [
                {
                  "type": "WORD",
                  "id": "db5c71ec-f893-4406-8a3f-e91ca6aa08dc",
                  "text": "Building"
                },
                {
                  "type": "WORD",
                  "id": "84e164c7-1fd0-4f18-b120-8a4f7563741b",
                  "text": "Search"
                },
                {
                  "type": "WORD",
                  "id": "2e54b4b1-ed52-4689-8f5f-0a06ec8a35b5",
                  "text": "Applications"
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```
