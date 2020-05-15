#   NLG APIs

*   [**Document Plans**](#document-plans)
    *   [GET /document-plans](#list-document-plans)
    *   [POST /document-plans](#create-a-document-plan)
    *   [PUT /document-plans/{id}](#update-a-document-plan)
    *   [GET /document-plans/{id}](#get-a-document-plan)
    *   [DELETE /document-plans/{id}](#delete-a-document-plan)
*   [**Data Samples**](#data-samples)
    *   [GET /data-samples](#list-data-samples)
    *   [POST /data-samples](#create-a-data-sample)
    *   [PUT /data-samples/{id}](#update-a-data-sample)
    *   [GET /data-samples/{id}](#get-a-data-sample)
    *   [DELETE /data-samples/{id}](#delete-a-data-sample)
*   [**Text Variants**](#text-variants)
    *   [POST /text-variants/from-data-sample](#create-variants-from-a-data-sample)
    *   [POST /text-variants/from-record](#create-variants-from-a-record)


##  Document Plans

### List Document Plans

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
        id:     uuid-0001
        name:   Document Plan One
    -
        id:     uuid-0002
        name:   Document Plan Two
```

### Create a Document Plan

####    Request:
```yaml
# HTTP:
POST:           /document-plans
# Body:
name:           Document Plan One
blocklyXml:     "..."
gremlinCode:    "..."
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
id:             uuid-0001
name:           Document Plan One
blocklyXml:     "..."
gremlinCode:    "..."
```

### Update a Document Plan

####    Request:
```yaml
# HTTP:
PUT:            /document-plans/uuid-0002
# Body:
id:             uuid-0002
name:           Document Plan Two
blocklyXml:     "..."
gremlinCode:    "..."
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
id:             uuid-0002
name:           Document Plan Two
blocklyXml:     "..."
gremlinCode:    "..."
```

### Get a Document Plan

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
name:           Document Plan Three
blocklyXml:     "..."
gremlinCode:    "..."
```

### Delete a Document Plan

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

##  Data Samples

### List Data Samples 

####    Request
```yaml
# HTTP:
GET:            /data-samples
```

####    Response:
```yaml
# Headers:
Content-Type:   application/json
# Body:
items:
    -
        id:         uuid-0001
        filename:   t-shirts.csv
    -
        id:         uuid-0002
        filename:   hotels.csv
```

### Create a Data Sample

####    Request
```yaml
# HTTP:
POST:           /data-samples
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

####    Response
```yaml
# HTTP:
Status:         200
# Headers:
Content-Type:   text/csv
# Body:
"color","material"
"red","cotton"
"blue","wool"
```

### Update a Data Sample

####    Request
```yaml
# HTTP:
PUT:            /data-samples/uuid-0000
# Headers:
Content-Type: multipart/form-data; boundary=----RandomString1234567890
# Body:
------RandomString1234567890
Content-Disposition: form-data; name="data_sample"; filename="t-shirts2.csv"
Content-Type: text/csv

"color","material"
"red","cotton"
"blue","wool"
------RandomString1234567890--
```

####    Response
```yaml
# HTTP:
Status:         200
# Headers:
Content-Type:   text/csv
# Body:
"color","material"
"red","cotton"
"blue","wool"
```

### Get a Data Sample

####    Request
```yaml
# HTTP:
GET:            /data-samples/uuid-0000
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

### Delete a Data Sample

####    Request
```yaml
# HTTP:
DELETE:         /data-samples/uuid-0000
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

##  Text Variants

### Create Variants from a Data Sample

####    Request
```yaml
# HTTP:
POST:           /text-variants/from-data-sample
# Headers:
Accept:         application/json
Content-Type:   application/json
# Body:
documentPlanId: uuid-0001
dataSampleId:   uuid-0002
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

_See [full JSON example][Variants]._

_See [Annotated Text JSON (ATJ) docs][ATJ] for more details._

### Create Variants from a Record

####    Request
```yaml
# HTTP:
POST:           /text-variants/from-record
# Headers:
Accept:         application/json
Content-Type:   application/json
# Body:
documentPlanId: uuid-0001
record:
    attr1:      value1
    attrTwo:    another_value
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

_See [full JSON example][Variants]._

_See [Annotated Text JSON (ATJ) docs][ATJ] for more details._


[ATJ]:          annotated-text-json.md
[Variants]:     examples/variants-example.json
