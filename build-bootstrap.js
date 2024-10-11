/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

const fs = require('fs');
const path = require('path');

// Define the source paths for Bootstrap CSS and JS
const sourceBootstrapCss = path.join(__dirname, 'node_modules', 'bootstrap', 'dist', 'css', 'bootstrap.min.css');
const sourceBootstrapJs = path.join(__dirname, 'node_modules', 'bootstrap', 'dist', 'js', 'bootstrap.bundle.min.js');

// Define the source paths for Bootstrap Icons
const sourceIconsCss = path.join(__dirname, 'node_modules', 'bootstrap-icons', 'font', 'bootstrap-icons.css');
const sourceIconsFonts = path.join(__dirname, 'node_modules', 'bootstrap-icons', 'font', 'fonts');

// Define the target paths
const targetCssDir = path.join(__dirname, 'src', 'main', 'resources', 'static', 'css');
const targetJsDir = path.join(__dirname, 'src', 'main', 'resources', 'static', 'js');
const targetIconsCss = path.join(targetCssDir, 'bootstrap-icons.css');
const targetIconsFonts = path.join(__dirname, 'src', 'main', 'resources', 'static', 'fonts');

// Ensure the target directories exist
fs.mkdirSync(targetCssDir, { recursive: true });
fs.mkdirSync(targetJsDir, { recursive: true });
fs.mkdirSync(targetIconsFonts, { recursive: true });

// Copy the Bootstrap CSS and JS files
fs.copyFileSync(sourceBootstrapCss, path.join(targetCssDir, 'bootstrap.min.css'));
fs.copyFileSync(sourceBootstrapJs, path.join(targetJsDir, 'bootstrap.bundle.min.js'));

// Copy the Bootstrap Icons CSS file
fs.copyFileSync(sourceIconsCss, targetIconsCss);

// Copy the Bootstrap Icons fonts directory
fs.readdirSync(sourceIconsFonts).forEach(file => {
    const sourceFile = path.join(sourceIconsFonts, file);
    const targetFile = path.join(targetIconsFonts, file);
    fs.copyFileSync(sourceFile, targetFile);
});

console.log('Bootstrap and Bootstrap Icons files copied successfully!');