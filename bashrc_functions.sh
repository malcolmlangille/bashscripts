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

# Use this command to set 
function setgitemail() {
        git add -A
        git commit -m "$1"
        git push
}

# Add idea /bin to the path to allow the opening of files in the editor using the idea command on the commandline
export PATH="/idea-server/bin:$PATH"

# Source the Git prompt script from the specified location.
. /usr/share/git-core/contrib/completion/git-prompt.sh

# Define color variables (optional)
GREEN="\[\033[01;32m\]"
YELLOW="\[\033[01;33m\]"
RESET="\[\033[00m\]"

# Customize the PS1 prompt:
# - \w prints the full working directory (e.g. /projects/scripts).
# - __git_ps1 prints the current git branch if inside a git repository.
export PS1="${GREEN}\w${RESET}\$(__git_ps1 ' (${YELLOW}%s${RESET})') \$ "



