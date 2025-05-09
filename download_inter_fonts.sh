#!/bin/bash

# Create font directory if it doesn't exist
FONT_DIR="app/src/main/res/font"
mkdir -p $FONT_DIR

# Download Inter font files
echo "Downloading Inter font files..."

# Regular fonts
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Thin.woff2" -o "$FONT_DIR/inter_thin.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-ExtraLight.woff2" -o "$FONT_DIR/inter_extra_light.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Light.woff2" -o "$FONT_DIR/inter_light.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Regular.woff2" -o "$FONT_DIR/inter_regular.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Medium.woff2" -o "$FONT_DIR/inter_medium.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-SemiBold.woff2" -o "$FONT_DIR/inter_semi_bold.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Bold.woff2" -o "$FONT_DIR/inter_bold.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-ExtraBold.woff2" -o "$FONT_DIR/inter_extra_bold.ttf"
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Black.woff2" -o "$FONT_DIR/inter_black.ttf"

# Italic font
curl -L "https://github.com/rsms/inter/raw/master/docs/font-files/Inter-Italic.woff2" -o "$FONT_DIR/inter_italic.ttf"

echo "Download complete!"
