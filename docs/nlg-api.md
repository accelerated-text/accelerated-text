---
title: nlg-api v
language_tabs:
- shell: Shell
  language_clients:
- shell: ""
  toc_footers: []
  includes: []
  search: true
  highlight_theme: darkula
  headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="">nlg-api v</h1>

> Scroll down for code samples, example requests and responses.

## POST /_graphql

*GraphQL endpoint*

Refer to ![GraphQl API](graphql.md)

## POST /nlg/

*Registers document plan for generation*

> Code samples

```shell
curl -X POST /nlg/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'
```

> Body example

```json
{
  "documentPlanName": "MyDocumentPlan",
  "dataRow": {
    "property1": "string",
    "property2": "string"
  },
  "readerFlagValues": {
    "Eng": true
  },
  "async": false
}
```

<h3 id="post__nlg_-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true|none|
|» documentPlanId|body|string|false|none|
|» documentPlanName|body|string|false|none|
|» dataId|body|string|false|none|
|» dataRow|body|object|false|none|
|»» **additionalProperties**|body|string|false|none|
|» sampleMethod|body|string|false|none|
|» readerFlagValues|body|object|false|none|
|»» **additionalProperties**|body|boolean|false|none|
|» async|body|boolean|false|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|» sampleMethod|relevant|
|» sampleMethod|first|

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

<h3 id="post__nlg_-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="post__nlg_-responseschema">Response Schema</h3>

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|none|none|
|» offset|integer(int64)|false|none|none|
|» totalCount|integer(int64)|false|none|none|
|» ready|boolean|false|none|none|
|» updatedAt|number(double)|false|none|none|
|» variants|[anyOf]|false|none|none|

*anyOf*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|string|false|none|none|

*or*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|object|false|none|none|

*continued*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» error|boolean|false|none|none|
|» message|string|false|none|none|

## POST /nlg/_bulk/

*Bulk generation*

> Code samples

```shell
curl -X POST /nlg/_bulk/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

> Body parameter

```json
{
  "documentPlanName": "string",
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
}
```

<h3 id="post__nlg__bulk_-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true|none|
|» dataRows|body|object|true|none|
|»» api.nlg.service.request/dataRow|body|object|false|none|
|»»» **additionalProperties**|body|string|false|none|
|» documentPlanId|body|string|false|none|
|» documentPlanName|body|string|false|none|
|» readerFlagValues|body|object|false|none|
|»» **additionalProperties**|body|boolean|false|none|

> Example responses

```json
{
  "resultIds": [
    "30619541-1033-479f-b8ff-af2c21b080fb",
    "7ea468d9-d2c3-46b2-af1d-a998d397a8c8"
  ]
}
```

<h3 id="post__nlg__bulk_-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="post__nlg__bulk_-responseschema">Response Schema</h3>

Status Code **200**

*api.nlg.service/generate-response-bulk*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultIds|[string]|true|none|none|

## GET /nlg/{id}

*Get NLG result*

> Code samples

```shell
curl -X GET /nlg/{id} \
  -H 'Accept: application/json'

```

<h3 id="get__nlg_{id}-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|format|query|string|false|none|
|id|path|string|true|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|format|annotated-text-shallow|
|format|annotated-text|
|format|raw|

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

<h3 id="get__nlg_{id}-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="get__nlg_{id}-responseschema">Response Schema</h3>

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|none|none|
|» offset|integer(int64)|false|none|none|
|» totalCount|integer(int64)|false|none|none|
|» ready|boolean|false|none|none|
|» updatedAt|number(double)|false|none|none|
|» variants|[anyOf]|false|none|none|

*anyOf*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|string|false|none|none|

*or*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|object|false|none|none|

*continued*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» error|boolean|false|none|none|
|» message|string|false|none|none|

## DELETE /nlg/{id}

*Delete NLG result*

> Code samples

```shell
curl -X DELETE /nlg/{id} \
  -H 'Accept: application/json'
```

<h3 id="delete__nlg_{id}-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string|true|none|

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

<h3 id="delete__nlg_{id}-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="delete__nlg_{id}-responseschema">Response Schema</h3>

Status Code **200**

*api.nlg.service/generate-response*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» resultId|string|true|none|none|
|» offset|integer(int64)|false|none|none|
|» totalCount|integer(int64)|false|none|none|
|» ready|boolean|false|none|none|
|» updatedAt|number(double)|false|none|none|
|» variants|[anyOf]|false|none|none|

*anyOf*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|string|false|none|none|

*or*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|»» *anonymous*|object|false|none|none|

*continued*

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» error|boolean|false|none|none|
|» message|string|false|none|none|

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

<h3 id="post__accelerated-text-data-files_-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|false|none|
|» file|body|string(binary)|false|none|

> Example responses

```json
{
  "message": "Succesfully uploaded file",
  "id": "b01970e3-8a66-4881-ad88-312c22be3c85"
}
```

<h3 id="post__accelerated-text-data-files_-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="post__accelerated-text-data-files_-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» message|string|true|none|none|
|» id|string|true|none|none|

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

<h3 id="get__health-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="get__health-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» health|string|true|none|none|

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

<h3 id="get__status-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

<h3 id="get__status-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|» color|string|true|none|none|
|» services|object|true|none|none|
