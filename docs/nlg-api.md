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

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

<h1 id="-default">Default</h1>

## post___graphql

> Code samples

```shell
# You can also use wget
curl -X POST /_graphql \
  -H 'Content-Type: application/json'

```

`POST /_graphql`

*GraphQL endpoint*

> Body parameter

```json
{}
```

<h3 id="post___graphql-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true|none|

<h3 id="post___graphql-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|default|Default|none|None|

<aside class="success">
This operation does not require authentication
</aside>

## post__nlg_

> Code samples

```shell
# You can also use wget
curl -X POST /nlg/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

`POST /nlg/`

*Registers document plan for generation*

> Body parameter

```json
{
  "documentPlanId": "string",
  "documentPlanName": "string",
  "dataId": "string",
  "dataRow": {
    "property1": "string",
    "property2": "string"
  },
  "sampleMethod": "relevant",
  "readerFlagValues": {
    "property1": true,
    "property2": true
  },
  "async": true
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

> 200 Response

```json
{
  "resultId": "string",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [
    "string"
  ],
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

<aside class="success">
This operation does not require authentication
</aside>

## post__nlg__bulk_

> Code samples

```shell
# You can also use wget
curl -X POST /nlg/_bulk/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

`POST /nlg/_bulk/`

*Bulk generation*

> Body parameter

```json
{
  "dataRows": {
    "property1": {
      "property1": "string",
      "property2": "string"
    },
    "property2": {
      "property1": "string",
      "property2": "string"
    }
  },
  "documentPlanId": "string",
  "documentPlanName": "string",
  "readerFlagValues": {
    "property1": true,
    "property2": true
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
    "d881852b-8b33-4213-95a1-118ce9ec816d",
    "44f797a0-bdf7-4ce3-a5c2-0bc058e54904",
    "4a09300a-4e24-4d72-9569-f1c52c7e5f5b",
    "3ec1d762-e632-40f2-b8d7-88f4450e07dd",
    "843711ca-26e9-4627-9b23-038c022cf2ed",
    "54c6a41c-4b28-4cd6-91ab-d4ac7f2b4e69",
    "87d43ffa-e671-4029-b81f-08eeedb59372",
    "5c1379cf-d1f4-456e-ba88-ebdf2fde1f84",
    "a63eb1e0-af28-4f7a-9803-e18cdc70f5cb",
    "c9e244dd-6170-4929-91a9-ab88fdf23e6e"
  ]
}
```

> 200 Response

```json
{
  "resultIds": [
    "string"
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

<aside class="success">
This operation does not require authentication
</aside>

## get__nlg_{id}

> Code samples

```shell
# You can also use wget
curl -X GET /nlg/{id} \
  -H 'Accept: application/json'

```

`GET /nlg/{id}`

*Get NLG result*

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

> 200 Response

```json
{
  "resultId": "string",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [
    "string"
  ],
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

<aside class="success">
This operation does not require authentication
</aside>

## delete__nlg_{id}

> Code samples

```shell
# You can also use wget
curl -X DELETE /nlg/{id} \
  -H 'Accept: application/json'

```

`DELETE /nlg/{id}`

*Delete NLG result*

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

> 200 Response

```json
{
  "resultId": "string",
  "offset": 0,
  "totalCount": 0,
  "ready": true,
  "updatedAt": 0,
  "variants": [
    "string"
  ],
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

<aside class="success">
This operation does not require authentication
</aside>

## post__accelerated-text-data-files_

> Code samples

```shell
# You can also use wget
curl -X POST /accelerated-text-data-files/ \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Accept: application/json'

```

`POST /accelerated-text-data-files/`

*Upload a file*

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

> 200 Response

```json
{
  "message": "string",
  "id": "string"
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

<aside class="success">
This operation does not require authentication
</aside>

## get__health

> Code samples

```shell
# You can also use wget
curl -X GET /health \
  -H 'Accept: application/json'

```

`GET /health`

*Check API health*

> Example responses

```json
{
  "health": "Ok"
}
```

> 200 Response

```json
{
  "health": "string"
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

<aside class="success">
This operation does not require authentication
</aside>

## get__status

> Code samples

```shell
# You can also use wget
curl -X GET /status \
  -H 'Accept: application/json'

```

`GET /status`

*Check service status*

> Example responses

```json
{
  "color": "green",
  "services": {
    "service": true
  }
}
```

> 200 Response

```json
{
  "color": "string",
  "services": {}
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

<aside class="success">
This operation does not require authentication
</aside>

