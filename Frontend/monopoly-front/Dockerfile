
FROM node:18 AS build
WORKDIR /app

COPY monopoly-frontend /app

RUN npm install

RUN npm run build


FROM nginx:stable
WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

COPY --from=build /app/dist .

COPY monopoly-frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]