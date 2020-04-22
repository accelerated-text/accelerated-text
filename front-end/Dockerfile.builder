FROM node:11-slim as builder

RUN apt-get update -qq && apt-get install -y -qq build-essential chromium libatk-bridge2.0-0 libgtk-3-0 rsync

ARG ACC_TEXT_API_URL
ARG ACC_TEXT_GRAPHQL_URL

ENV ACC_TEXT_API_URL=$ACC_TEXT_API_URL
ENV ACC_TEXT_GRAPHQL_URL=$ACC_TEXT_GRAPHQL_URL

WORKDIR /usr/src/app
COPY front-end/package.json /usr/src/app

COPY front-end/ /usr/src/app/dp
COPY front-end/ /usr/src/app/amr
COPY front-end/ /usr/src/app/rgl

COPY api/resources/schema.graphql /usr/src/app/dp/packages/graphql/schema.graphql
COPY api/resources/schema.graphql /usr/src/app/amr/packages/graphql/schema.graphql
COPY api/resources/schema.graphql /usr/src/app/rgl/packages/graphql/schema.graphql



RUN mv rgl/packages/plan-editor/RglSidebar.js rgl/packages/plan-editor/Sidebar.js && \
    mv  rgl/packages/nlg-blocks/One-of.js rgl/packages/nlg-blocks/One-of-synonyms.js && \
    mv  rgl/packages/nlg-blocks/Frame.js rgl/packages/nlg-blocks/Segment.js && \
    mv  rgl/packages/nlg-blocks/RGL-plan.js rgl/packages/nlg-blocks/Document-plan.js && \
    mv  rgl/packages/plan-selector/RglPlanSelector.js rgl/packages/plan-selector/PlanSelector.js && \
    mv  rgl/packages/onboard-code/RglOnboardCode.js rgl/packages/onboard-code/OnboardCode.js && \
    mv  rgl/packages/plan-editor/RglPlanEditor.js rgl/packages/plan-editor/PlanEditor.js && \
    mv  rgl/packages/document-plans/rgl-plan-template.js rgl/packages/document-plans/plan-template.js && \
    mv  rgl/packages/header/RglHeader.js rgl/packages/header/Header.js && \
    mv  rgl/packages/webpack/rgl.config.js rgl/packages/webpack/config.js && \
    mv  rgl/packages/graphql/rgl-queries.graphql rgl/packages/graphql/queries.graphql

RUN mv  amr/packages/plan-editor/ParadigmsSidebar.js amr/packages/plan-editor/Sidebar.js && \
    mv  amr/packages/nlg-blocks/One-of.js amr/packages/nlg-blocks/One-of-synonyms.js && \
    mv  amr/packages/nlg-blocks/Frame.js amr/packages/nlg-blocks/Segment.js && \
    mv  amr/packages/nlg-blocks/AMR-plan.js amr/packages/nlg-blocks/Document-plan.js && \
    mv  amr/packages/plan-selector/ParadigmsPlanSelector.js amr/packages/plan-selector/PlanSelector.js && \
    mv  amr/packages/onboard-code/ParadigmsOnboardCode.js amr/packages/onboard-code/OnboardCode.js && \
    mv  amr/packages/plan-editor/ParadigmsPlanEditor.js amr/packages/plan-editor/PlanEditor.js && \
    mv  amr/packages/document-plans/paradigms-plan-template.js amr/packages/document-plans/plan-template.js && \
    mv  amr/packages/header/ParadigmsHeader.js amr/packages/header/Header.js && \
    mv  amr/packages/webpack/paradigms.config.js amr/packages/webpack/config.js && \
    mv  amr/packages/graphql/paradigms-queries.graphql amr/packages/graphql/queries.graphql






RUN rm -rf /usr/src/app/dp/node_modules
RUN rm -rf /usr/src/app/amr/node_modules
RUN rm -rf /usr/src/app/rgl/node_modules

RUN mkdir -p /opt/dist
RUN mkdir -p /opt/dist/dp  && cd dp  && make build && cp -r dist/* /opt/dist/dp
RUN mkdir -p /opt/dist/amr  && cd amr  && make build && cp -r dist/* /opt/dist/amr
RUN mkdir -p /opt/dist/rgl  && cd rgl  && make build && cp -r dist/* /opt/dist/rgl



FROM nginx:latest

RUN mkdir -p /var/www/acc-text/

COPY --from=builder /opt/dist /var/www/acc-text

COPY front-end/nginx.conf /etc/nginx/nginx.conf
