#!/bin/bash
# Script to run ansible playbooks that live in a world writable directory.
# See https://docs.ansible.com/ansible/devel/reference_appendices/config.html#cfg-in-world-writable-dir
# Usage: ./run_playbook_under_wsl.sh <playbook.yml> [--tags <tag1,tag2>]

# Check if playbook is specified
if [ $# -eq 0 ]; then
    echo "Playbook is not specified."
    echo "Exit with code 1."
    exit 1
fi

playbook="$1"
tags=""

shift 1  # Shift the first argument (playbook) out of the argument list
if [ "$#" -gt 0 ]; then
    case $1 in
        --tags)
            tags="$2"
            ;;
        *)
            echo "Unknown option: $1"
            echo "Exit with code 1."
            exit 1
            ;;
    esac
fi

if [ "$tags" == "" ]; then
    ANSIBLE_CONFIG="./ansible.cfg" ansible-playbook "$playbook"
else
    ANSIBLE_CONFIG="./ansible.cfg" ansible-playbook "$playbook" --tags "$tags"
fi
unset ANSIBLE_CONFIG
