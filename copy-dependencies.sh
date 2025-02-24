#!/bin/bash
set -euo pipefail

# Colores para mensajes
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
NC="\033[0m"  # Sin color

# Función para imprimir con timestamp
log() {
    echo -e "$(date +'%Y-%m-%d %H:%M:%S') $1"
}

log "${GREEN}📦 Iniciando copia de artefactos para microservicios...${NC}"

# Configuración base
REPO_BASE="$HOME/.m2/repository/com/kynsoft/finamer"
VERSION="0.0.1-SNAPSHOT"

# Si no se pasan argumentos, definir un conjunto por defecto
if [ "$#" -eq 0 ]; then
    microservices=("cloudBridges" "creditCard" "gateway" "identity" "insis" "invoicing" "payment" "report" "scheduler" "settings" "tcaInnsist")
else
    microservices=("$@")
fi

# Función para copiar un artefacto (JAR y POM) en un directorio destino
copy_artifact() {
    local artifact="$1"
    local dest_dir="$2"
    local source_dir="$REPO_BASE/$artifact/$VERSION"
    local source_jar="$source_dir/${artifact}-${VERSION}.jar"
    local source_pom="$source_dir/${artifact}-${VERSION}.pom"
    local dest_jar="$dest_dir/${artifact}-${VERSION}.jar"
    local dest_pom="$dest_dir/${artifact}-${VERSION}.pom"

    log "${YELLOW}📋 Procesando artefacto: ${artifact} -> ${dest_dir}${NC}"

    if [ -f "$source_jar" ]; then
        cp "$source_jar" "$dest_jar"
        log "${GREEN}✅ Copiado exitoso del JAR: $source_jar -> $dest_jar${NC}"
    else
        log "${RED}❌ ERROR: No se encontró el JAR para ${artifact} en $source_jar${NC}"
        exit 1
    fi

    if [ -f "$source_pom" ]; then
        cp "$source_pom" "$dest_pom"
        log "${GREEN}✅ Copiado exitoso del POM: $source_pom -> $dest_pom${NC}"
    else
        log "${YELLOW}⚠️  Advertencia: No se encontró el POM para ${artifact} en $source_dir${NC}"
    fi
}

# Iterar sobre los microservicios
for ms in "${microservices[@]}"; do
    # Definir la carpeta destino para cada microservicio (por ejemplo, settings/libs)
    DEST_DIR="$ms/libs"
    mkdir -p "$DEST_DIR"
    log "${YELLOW}🔹 Procesando microservicio: $ms. Destino: $DEST_DIR${NC}"

    # Asignar artefactos según el microservicio mediante condicionales
    if [[ "$ms" == "cloudBridges"
    || "$ms" == "gateway"
    ||  "$ms" == "identity"
    ||  "$ms" == "insis"
    ||  "$ms" == "report"
    ||  "$ms" == "scheduler"
    ||  "$ms" == "tcaInnsist" ]]; then
        artifacts=("share")
    elif [[ "$ms" == "creditCard"
    ||  "$ms" == "invoicing"
    ||  "$ms" == "payment"
    ||  "$ms" == "settings" ]]; then
        artifacts=("share" "audit-agent")
    else
        log "${YELLOW}⚠️ No se definió un mapeo de artefactos para el microservicio: $ms. Saltando...${NC}"
        continue
    fi

    # Iterar sobre los artefactos y copiarlos
    for artifact in "${artifacts[@]}"; do
        copy_artifact "$artifact" "$DEST_DIR"
    done
done

log "${GREEN}✅ Todos los artefactos se copiaron correctamente.${NC}"