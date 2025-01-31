historyg() {
    if [[ -z "$1" ]]; then
        echo "Usage: hgrep <search_word>"
        return 1
    fi
    history | grep --color=always -i "$1"
}

historyx() {
    if [[ -z "$1" ]]; then
        echo "Usage: historyx <history_number>"
        return 1
    fi

    # Extract command from history
    local cmd=$(history | awk -v num="$1" '$1 == num {sub(/^[0-9]+[[:space:]]*/, "", $0); print $0}')

    # If command found, populate it in the prompt
    if [[ -n "$cmd" ]]; then
        echo "$cmd"  # Show the command
        READLINE_LINE="$cmd"  # Populate it in the command prompt
        READLINE_POINT=${#cmd}  # Move cursor to end
    else
        echo "No command found for history number $1"
        return 1
    fi
}

