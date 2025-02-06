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

# Define color variables.
GREEN="\[\033[01;32m\]"       # Bright green for folder names.
DARK_GREEN="\[\033[0;32m\]"   # Dark green for the "/" separators.
YELLOW="\[\033[01;33m\]"      # Yellow for Git branch name.
RESET="\[\033[00m\]"

# Define a function that outputs the full working directory with
# "/" replaced by dark green and folder names in bright green.
colored_w() {
    # Prepend bright green color and then replace every "/" with
    # dark green "/" followed by bright green again.
    echo "${GREEN}${PWD//\//${DARK_GREEN}/${GREEN}}"
}

# Customize the PS1 prompt:
# - $(colored_w) calls our function to display the colored full path.
# - __git_ps1 adds the current Git branch (if available).
export PS1="\$(colored_w)${RESET}\$(__git_ps1 ' (${YELLOW}%s${RESET})') \$ "




