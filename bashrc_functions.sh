historyg() {
    if [[ -z "$1" ]]; then
        echo "Usage: hgrep <search_word>"
        return 1
    fi
    history | grep --color=always -i "$1"
}

historyx() {
    local cmd_num=$1
    if [[ -z "$cmd_num" ]]; then
        echo "Usage: populate_command <history_number>"
        return 1
    fi
    # Retrieve the command from history and insert it into the prompt
    READLINE_LINE=$(history | awk -v num="$cmd_num" '$1 == num {sub(/^[0-9]+[[:space:]]*/, "", $0); print $0}')
    READLINE_POINT=${#READLINE_LINE}
}
