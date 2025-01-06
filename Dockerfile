ARG NODE_VERSION=node:20.17.0

FROM $NODE_VERSION AS dependency-base

# create destination directory
RUN mkdir -p /app
WORKDIR /app

# copy the app, note .dockerignore
COPY package.json package-lock.json ./
# Limpiar caché e instalar dependencias
RUN npm cache clean --force && \
    npm ci && \
    npm cache clean --force

FROM dependency-base AS production-base

# copy all files
COPY . .

# Actualizar npm y configurar memoria
RUN npm install -g npm@11.0.0
# Configurar variables de entorno para manejar archivos y memoria
ENV NODE_OPTIONS="--max-old-space-size=8192"
ENV UV_THREADPOOL_SIZE=64
ENV NODE_MAX_OLD_SPACE_SIZE=8192

RUN npm cache clean --force && \
    npm ci && \
    npm cache clean --force

FROM $NODE_VERSION-slim AS production

WORKDIR /app

# Copy solo los archivos necesarios
COPY --from=production-base /app/.output /app/.output

# Service hostname and port
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
ENV PORT=3000
ENV NODE_ENV=production

# start the app
CMD [ "node", "/app/.output/server/index.mjs" ]
