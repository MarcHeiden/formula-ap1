#!/bin/bash
# Script to remove *Controller, TeamsOfSeasons and DriversOfRaces tags from the generated public API documentation.
# Usage: ./remove-tags.sh <openapi-file.yml>

# Check if filename parameter was specified
if [ $# -eq 0 ]; then
    echo "Filename parameter was not specified."
    echo "Exit with code 1."
    exit 1
fi

patterns=("Controller" "TeamsOfSeasons" "DriversOfRaces")
filename="$1"

for pattern in "${patterns[@]}"; do
    sed -i "/$pattern/d" "$filename"
done

echo "Removed tags from '$filename'."
