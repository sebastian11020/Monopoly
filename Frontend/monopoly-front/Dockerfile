FROM node:18 AS build
WORKDIR /app

COPY . .

RUN npm install

RUN npm run build

FROM nginx:stable
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=build /app/dist .
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
