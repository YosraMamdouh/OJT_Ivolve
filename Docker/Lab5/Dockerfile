FROM node:22-alpine
RUN npm install -g npm@latest

WORKDIR /app

COPY package.json ./

RUN npm install

COPY app.js ./

EXPOSE 3000

CMD ["npm", "start"]
