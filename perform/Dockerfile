FROM node:10-slim

RUN apt-get update -qq && apt-get install -y -qq build-essential chromium libatk-bridge2.0-0 libgtk-3-0 rsync
RUN apt-get install -y python3-pip
RUN pip3 install awscli --upgrade

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app

RUN npm install package.json && touch -m node_modules

CMD ["npm", "start"]
