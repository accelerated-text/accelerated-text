FROM node:11-slim

# RUN apt-get update -qq && apt-get install -y -qq build-essential 

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app

RUN npm install && touch -m node_modules

CMD ["npm", "start"]
