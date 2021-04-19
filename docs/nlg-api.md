<!-- Generator: Widdershins v4.0.1 -->

# nlg-api

Open [http://localhost:3001](http://localhost:3001) to access [Swagger UI](https://swagger.io/tools/swagger-ui/).

Scroll down for code samples, example requests and responses.

## POST /_graphql

*GraphQL endpoint*

Refer to [GraphQl API](graphql.md).

## POST /nlg/

*Registers document plan for generation*

```shell
curl -X POST /nlg/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{"documentPlanName": "MyDocumentPlan",
       "dataRow": {
         "property1": "string",
         "property2": "string"
       },
       "readerFlagValues": {
         "Eng": true
       },
       "async": false
     }'
```

### Parameters

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true||
|» documentPlanId|body|string|false|either id or document plan name must be provided|
|» documentPlanName|body|string|false||
|» dataId|body|string|false|id of existing data file|
|» dataRow|body|object|false|actual cells and their values|
|»» **additionalProperties**|body|string|false||
|» sampleMethod|body|string|false|only valid, when generating with existing data file|
|» readerFlagValues|body|object|false|defines enabled languages and reader models|
|»» **additionalProperties**|body|boolean|false||
|» async|body|boolean|false|when enabled, returns result id instantly without waiting for generation to complete|

<br/>

#### Enumerated Values

|Parameter|Value|
|---|---|
|» sampleMethod|relevant|
|» sampleMethod|first|

<br/>

> Example responses

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 1,
  "ready": true,
  "updatedAt": 1618818434,
  "variants": [
    "Text value."
  ]
}
```

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [],
  "error": true,
  "message": "string"
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|||
|» offset|integer(int64)|false|||
|» totalCount|integer(int64)|false|||
|» ready|boolean|false|||
|» updatedAt|number(double)|false|||
|» variants|[anyOf]|false|||
|» error|boolean|false|||
|» message|string|false|||

<br/>

## POST /nlg/_bulk/

*Bulk generation*

```shell
curl -X POST /nlg/_bulk/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{"documentPlanName": "string",
       "dataRows": {
         "30619541-1033-479f-b8ff-af2c21b080fb": {
           "property1": "string",
           "property2": "string"
         },
         "7ea468d9-d2c3-46b2-af1d-a998d397a8c8": {
           "property1": "string",
           "property2": "string"
         }
       },
       "readerFlagValues": {
         "Eng": true
       }
      }'
```

### Parameters

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true||
|» dataRows|body|object|true||
|»» api.nlg.service.request/dataRow|body|object|false||
|»»» **additionalProperties**|body|string|false||
|» documentPlanId|body|string|false||
|» documentPlanName|body|string|false||
|» readerFlagValues|body|object|false||
|»» **additionalProperties**|body|boolean|false||

<br/>

> Example responses

```json
{
  "resultIds": [
    "30619541-1033-479f-b8ff-af2c21b080fb",
    "7ea468d9-d2c3-46b2-af1d-a998d397a8c8"
  ]
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

*api.nlg.service/generate-response-bulk*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultIds|[string]|true|||

<br/>

## GET /nlg/{id}

*Get NLG result*

> Code samples

```shell
curl -X GET /nlg/{id} \
  -H 'Accept: application/json'

```

### Parameters

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|format|query|string|false||
|id|path|string|true||

<br/>

#### Enumerated Values

|Parameter|Value|
|---|---|
|format|annotated-text-shallow|
|format|annotated-text|
|format|raw|

<br/>

> Example responses

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 1,
  "ready": true,
  "updatedAt": 1618818434,
  "variants": [
    "Text value."
  ]
}
```

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [],
  "error": true,
  "message": "string"
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|||
|» offset|integer(int64)|false|||
|» totalCount|integer(int64)|false|||
|» ready|boolean|false|||
|» updatedAt|number(double)|false|||
|» variants|[anyOf]|false|||
|» error|boolean|false|||
|» message|string|false|||

<br/>

## DELETE /nlg/{id}

*Delete NLG result*

> Code samples

```shell
curl -X DELETE /nlg/{id} \
  -H 'Accept: application/json'
```

### Parameters

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string|true||

<br/>

> Example responses

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 1,
  "ready": true,
  "updatedAt": 1618818434,
  "variants": [
    "Text value."
  ]
}
```

```json
{
  "resultId": "cc17275a-67ac-4403-9b1c-840a19dd344f",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [],
  "error": true,
  "message": "string"
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|||
|» offset|integer(int64)|false|||
|» totalCount|integer(int64)|false|||
|» ready|boolean|false|||
|» updatedAt|number(double)|false|||
|» variants|[anyOf]|false|||
|» error|boolean|false|||
|» message|string|false|||

<br/>

## POST /accelerated-text-data-files/

*Upload a file*

> Code samples

```shell
curl -X POST /accelerated-text-data-files/ \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Accept: application/json'

```

> Body parameter

```yaml
file: string

```

### Parameters

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|false||
|» file|body|string(binary)|false||

<br/>

> Example responses

```json
{
  "message": "Succesfully uploaded file",
  "id": "b01970e3-8a66-4881-ad88-312c22be3c85"
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» message|string|true|||
|» id|string|true|||

<br/>

## GET /health

*Check API health*

> Code samples

```shell
curl -X GET /health \
  -H 'Accept: application/json'

```

> Example responses

```json
{
  "health": "Ok"
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» health|string|true|||

<br/>

## GET /status

*Check service status*

> Code samples

```shell
curl -X GET /status \
  -H 'Accept: application/json'

```

> Example responses

```json
{
  "color": "green",
  "services": {
    "service": true
  }
}
```

### Responses

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|OK||Inline|

<br/>

### Response Schema

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» color|string|true|||
|» services|object|true|||

<br/>
