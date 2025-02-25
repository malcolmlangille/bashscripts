#!/bin/bash
# extract_and_generate_gradle.sh
#
# This script extracts Maven dependency details from a pom.xml file using awk,
# writes them to a CSV file, and then generates a build.gradle file using the CSV data.
#
# Usage:
#   ./extract_and_generate_gradle.sh [pom.xml] [dependencies.csv] [build.gradle]
#
# Defaults:
#   pom.xml, dependencies.csv, build.gradle
#
# Note: This script uses only standard tools (awk, sed, grep) available on most systems.

# Set default filenames from command-line arguments or use defaults
POM_FILE="${1:-pom.xml}"
CSV_FILE="${2:-dependencies.csv}"
GRADLE_FILE="${3:-build.gradle}"

# Verify that the pom.xml file exists
if [ ! -f "$POM_FILE" ]; then
  echo "Error: Input file '$POM_FILE' not found!"
  exit 1
fi

echo "Extracting dependencies from '$POM_FILE' to '$CSV_FILE'..."

# Write CSV header to the output file
echo "groupId,artifactId,version,scope,type" > "$CSV_FILE"

# Extract dependency details from the pom.xml file using awk.
# The record separator is set to "</dependency>" so that each dependency block is processed as one record.
awk '
BEGIN {
  RS="</dependency>";
  OFS=","
}
{
  # Process only records containing a <dependency> tag.
  if (index($0, "<dependency>") == 0)
    next;

  # Initialize fields (default to empty)
  groupId=""; artifactId=""; version=""; scope=""; type="";

  # Extract each field using regex. Extra whitespace is allowed.
  if (match($0, /<groupId>[[:space:]]*([^<]+)[[:space:]]*<\/groupId>/, a))
    groupId = a[1];
  if (match($0, /<artifactId>[[:space:]]*([^<]+)[[:space:]]*<\/artifactId>/, a))
    artifactId = a[1];
  if (match($0, /<version>[[:space:]]*([^<]+)[[:space:]]*<\/version>/, a))
    version = a[1];
  if (match($0, /<scope>[[:space:]]*([^<]+)[[:space:]]*<\/scope>/, a))
    scope = a[1];
  if (match($0, /<type>[[:space:]]*([^<]+)[[:space:]]*<\/type>/, a))
    type = a[1];

  # Output the extracted fields as a CSV row
  print groupId, artifactId, version, scope, type;
}
' "$POM_FILE" >> "$CSV_FILE"

echo "CSV file '$CSV_FILE' generated successfully."

echo "Generating Gradle build file '$GRADLE_FILE' from '$CSV_FILE'..."

# Start writing the build.gradle file with a basic header
cat > "$GRADLE_FILE" << 'EOF'
plugins {
    id 'java'
}

repositories {
    mavenCentral()
}

dependencies {
EOF

# Process the CSV file (skip header) to append dependency lines
tail -n +2 "$CSV_FILE" | while IFS=, read -r groupId artifactId version scope type; do
  # Create dependency notation: group:artifact:version
  dependencyStr="${groupId}:${artifactId}:${version}"

  # Map Maven scope to Gradle configuration:
  # For "test" scope use testImplementation; default to implementation.
  if [ "$scope" = "test" ]; then
    config="testImplementation"
  else
    config="implementation"
  fi

  echo "    ${config} '${dependencyStr}'" >> "$GRADLE_FILE"
done

# Close the dependencies block
echo "}" >> "$GRADLE_FILE"

echo "Gradle build file '$GRADLE_FILE' generated successfully."
