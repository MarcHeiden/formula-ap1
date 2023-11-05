#!/bin/bash
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
