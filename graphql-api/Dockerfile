FROM registry.gitlab.com/tokenmill/nlg/accelerated-text/graphql-api:deps as deps

FROM oracle/graalvm-ce:19.1.1 as builder
RUN gu install native-image

COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /root/.ssh /root/.ssh
COPY --from=deps /root/.gitlibs /root/.gitlibs
COPY --from=deps /usr/local/sbin/clojure /usr/local/sbin/clojure

ENV GRAALVM_HOME=$JAVA_HOME

RUN yum -y install openssh-clients zip
RUN curl -O https://download.clojure.org/install/linux-install-1.10.0.442.sh
RUN chmod +x linux-install-1.10.0.442.sh
RUN ./linux-install-1.10.0.442.sh

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY data-access/ /usr/src/data-access
COPY graphql-api/ /usr/src/app

RUN clojure -A:native-image
RUN cp $JAVA_HOME/jre/lib/amd64/libsunec.so .
RUN cp target/app server
RUN chmod 755 server bootstrap


FROM amazonlinux:2 as archiver

RUN yum -y install zip

WORKDIR /usr/src/app
COPY --from=builder /usr/src/app/bootstrap bootstrap
COPY --from=builder /usr/src/app/server server
COPY --from=builder /usr/src/app/libsunec.so libsunec.so
RUN zip function.zip bootstrap server libsunec.so


FROM amazonlinux:2 as deployer

RUN yum -y install awscli

ARG AWS_DEFAULT_REGION
ENV AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION

ARG AWS_ACCESS_KEY_ID
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID

ARG AWS_SECRET_ACCESS_KEY
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY

ARG S3_BUCKET=bucket-name
ENV S3_BUCKET=$S3_BUCKET

ARG S3_FOLDER=folder-name
ENV S3_FOLDER=$S3_FOLDER

ARG STACK_NAME=lambda-custom-runtime-graphqlApi
ENV STACK_NAME=$STACK_NAME

COPY --from=archiver /usr/src/app/function.zip function.zip
COPY graphql-api/lambda.yml lambda.yml

RUN aws cloudformation package --template-file lambda.yml --s3-bucket ${S3_BUCKET} --s3-prefix ${S3_FOLDER} --output-template-file /tmp/lambda-packaged.yml
RUN aws cloudformation deploy --template-file /tmp/lambda-packaged.yml --stack-name ${STACK_NAME} --capabilities CAPABILITY_IAM --no-fail-on-empty-changeset
