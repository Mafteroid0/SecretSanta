#!/bin/bash

set -e

EXPORT_DIR="$1"
PROJECT_DIR="/Users/daniilcelikov/IdeaProjects/SecretSanta"

TEMPLATES="$PROJECT_DIR/src/main/resources/templates"
STATIC="$PROJECT_DIR/src/main/resources/static"

mkdir -p "$TEMPLATES"
mkdir -p "$STATIC"

# Удаляем предыдущий экспорт Bootstrap Studio
find "$TEMPLATES" -maxdepth 1 -type f -name "*.html" -delete
#rm -rf "$STATIC/assets"

# HTML -> Thymeleaf templates
find "$EXPORT_DIR" -maxdepth 1 -type f -name "*.html" -exec cp {} "$TEMPLATES/" \;

# Assets -> Spring static
if [ -d "$EXPORT_DIR/assets" ]; then
    cp -R "$EXPORT_DIR/assets" "$STATIC/"
fi