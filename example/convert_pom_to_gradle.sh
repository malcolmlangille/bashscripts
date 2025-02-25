#!/bin/bash
# extract_dependencies.sh
#
# This script extracts Maven dependency details from a pom.xml file using awk.
# It outputs a CSV file with the following columns: groupId, artifactId, version, scope, and type.
#
# Usage:
#   ./extract_dependencies.sh [input_file] [output_file]
#
# Defaults:
#   input_file: pom.xml
#   output_file: dependencies.csv
#
# Note: This script uses only standard tools (awk, sed, grep) available on most systems.
# It works by setting the record separator to "</dependency>" and using regular expressions
# to extract the content between the corresponding XML tags.

# Set default input and output files (or use provided arguments)
INPUT_FILE="${1:-pom.xml}"
OUTPUT_FILE="${2:-dependencies.csv}"

# Verify the input file exists
if [ ! -f "$INPUT_FILE" ]; then
  echo "Error: Input file '$INPUT_FILE' not found!"
  exit 1
fi

# Write CSV header
echo "groupId,artifactId,version,scope,type" > "$OUTPUT_FILE"

# Use awk to extract dependency information.
# We set the record separator to "</dependency>" so that each dependency block is one record.
awk '
BEGIN {
  RS="</dependency>";
  OFS=","
}
{
  # Process only if the record contains a <dependency> tag
  if (index($0, "<dependency>") == 0)
    next;

  # Initialize variables for each field (default to empty)
  groupId=""; artifactId=""; version=""; scope=""; type="";

  # Use regular expressions to extract each tag value.
  # The regex allows for extra whitespace around the content.
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

  # Output the fields as a CSV row
  print groupId, artifactId, version, scope, type;
}
' "$INPUT_FILE" >> "$OUTPUT_FILE"

echo "Dependencies have been extracted to '$OUTPUT_FILE'."
