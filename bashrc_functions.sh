# .bashrc

# Source global definitions
if [ -f /etc/bashrc ]; then
        ./etc/bashrc
fi

# User specific environment
if ! [[ "$PATH" =~ "$HOME/.local/bin:$HOME/bin:"]]
then
    PATH="$HOME/.local/bin:$HOME/bin:$PATH"
fi
export PATH

# Uncomment the following line if you don't like systemctl's auto-paging feature:
# export SYSTEMD_PAGER=

# User specific aliases and functions
# Used to grep the history for a word and highlight it
historyg() {
    if [[ -z "$1" ]]; then
        echo "Usage: hgrep <search_word>"
        return 1
    fi
    history | grep --color=always -i "$1"
}

# Use this to get the command at history location 123
# !123:p to get command. Push up.

# Use this command to easily check in to git
function lazygit() {
        git add -A
        git commit -m "$1"
        git push
}

# Add idea /bin to the path to allow the opening of files in the editor using the idea command on the commandline
export PATH="/idea-server/bin:$PATH"


