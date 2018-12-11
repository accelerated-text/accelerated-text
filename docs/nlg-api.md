#   NLG APIs

*   [GET /document-plans](#list-document-plans)
*   [POST /document-plans](#create-a-document-plan)
*   [PUT /document-plans/{id}](#update-a-document-plan)
*   [GET /document-plans/{id}](#get-a-document-plan)
*   [DELETE /document-plans/{id}](#delete-a-document-plan)
*   [GET /document-plans/{id}/data-sample](#get-a-data-sample)
*   [POST /document-plans/{id}/data-sample](#set-a-data-sample)
*   [GET /document-plans/{id}/variants](#get-text-variants)

##  List Document Plans

####    Request:
```yaml
# HTTP:
GET:            /document-plans
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
items:
    -
        id:     plan-uuid-0001
        name:   Document Plan One
    -
        id:     plan-uuid-0002
        name:   Document Plan Two
```

##  Create a Document Plan

####    Request:
```yaml
# HTTP:
POST:           /document-plans
# Body:
name:           Human set name
blocks:         [ ...BWJ blocks... ]
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
id:             uuid-0001
name:           Human set name
blocks:         [ ...BWJ blocks... ]
```

_See [BWJ docs][BWJ] for more details._

##  Update a Document Plan

####    Request:
```yaml
# HTTP:
PUT:            /document-plans/uuid-0002
# Body:
id:             uuid-0002
name:           Human set name
blocks:         [ ...BWJ blocks... ]
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
id:             uuid-0002
name:           Human set name
blocks:         [ ...BWJ blocks... ]
```

_See [BWJ docs][BWJ] for more details._

##  Get a Document Plan

####    Request
```yaml
# HTTP:
GET:            /document-plans/uuid-0003
```

####    Response
```yaml
# Headers:
Content-Type:   application/json
# Body:
id:             uuid-0003
name:           Human set name
blocks:         [ ...BWJ blocks... ]
```

_See [BWJ docs][BWJ] for more details._

##  Delete a Document Plan

####    Request
```yaml
# HTTP:
DELETE:         /document-plans/uuid-0004
```

####    Response
```yaml
# HTTP:
Status:         200
# Headers:
Content-Type:   application/json
# Body:
"ok"
```

##  Get a Data Sample

####    Request
```yaml
# HTTP:
GET:            /document-plans/uuid-0005/data-sample
```

####    Response
```yaml
# Headers:
Content-Type:   text/csv
# Body:
"color","material"
"red","cotton"
"blue","wool"
```

##  Set a Data Sample

####    Request
```yaml
# HTTP:
POST:           /document-plans/uuid-0006/data-sample
# Headers:
Content-Type: multipart/form-data; boundary=----RandomString1234567890
# Body:
------RandomString1234567890
Content-Disposition: form-data; name="data_sample"; filename="t-shirts.csv"
Content-Type: text/csv

"color","material"
"red","cotton"
"blue","wool"
------RandomString1234567890--
```

##  Get Text Variants

####    Request
```yaml
# HTTP:
GET:            /document-plans/uuid-0007/text-variants
```

####    Response
```yaml
# Headers:
Content-Type:   application/json
# Body:
offset:         0
totalCount:     1234567
variants:       [ ...Annotated Text Elements... ]
```

_See [variant-examples.json][] for full example._

_See [Annotated Text JSON (ATJ) docs][ATJ] for more details._

[ATJ]:          annotated-text-json.md
[BWJ]:          blockly-workspace-json.md
