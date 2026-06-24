FROM node:20-alpine AS build
WORKDIR /app

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ .
RUN npm run build -- --configuration=production

FROM nginx:alpine

COPY frontend/default.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/frontend/ /usr/share/nginx/html/

EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]
