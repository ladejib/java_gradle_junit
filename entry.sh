#!/bin/bash
set -e

# Create classes directory if it doesn't exist
mkdir -p /app/classes

# Compile source code
echo "Compiling Java source..."
javac -d /app/classes /app/src/**/*.java

# Run the application
echo "Running Java application..."
java -cp /app/classes com.example.HelloWorld

