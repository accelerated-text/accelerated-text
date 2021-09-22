lint:
	clj -M:lint

fix-format:
	clojure -M:format fix

unit-tests:
	clojure -A:test -e:integration

integration-tests:
	docker-compose -p test -f docker-compose.test.yml down && \
	docker-compose -p test -f docker-compose.test.yml build && \
	docker-compose -p test -f docker-compose.test.yml up --remove-orphans --abort-on-container-exit --exit-code-from api

run-local:
	clojure -m api.server

export-document-plan:
	@echo "WARNING: This rule will be removed soon, check https://accelerated-text.readthedocs.io/en/latest/export/ for export instructions."
	@curl -s -X POST http://localhost:3001/_graphql \
	     -H "Content-Type: application/json" \
	     -d "{\"query\": \"{documentPlan(id:\\\"$(id)\\\",name:\\\"$(name)\\\",kind:\\\"Document\\\"){id uid name kind blocklyXml documentPlan}}\"}" | \
	jq -r '.data.documentPlan' | \
	jq '.documentPlan = (.documentPlan | fromjson)'

export-all-document-plans:
	@echo "WARNING: This rule will be removed soon, check https://accelerated-text.readthedocs.io/en/latest/export/ for export instructions."
	@curl -s -X POST http://localhost:3001/_graphql \
         -H "Content-Type: application/json" \
         -d "{\"query\": \"{documentPlans{items{id uid name kind blocklyXml documentPlan}}}\"}" | \
    jq -r '.data.documentPlans.items[] | @base64 ' | \
    while read -r item; \
    	do echo $${item} | \
    	   base64 --decode | \
    	   jq -r $${1} '.' | \
    	   jq '.documentPlan = (.documentPlan | fromjson)' | \
    	   cat > item.json.tmp && \
    	   mv item.json.tmp $${dir:=resources/document-plans}/$$(cat item.json.tmp | jq -r '.id').json  ; \
    done
