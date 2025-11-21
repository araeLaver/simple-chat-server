#!/bin/bash
# Clean Claude references from commit messages

cd "$(dirname "$0")"

# Get list of commits to rewrite (last 8 commits)
COMMITS=(25fb0ef d5fc720 7a9e9c7 55f1227 f3396a0 f672893 2519995 4157e7e)

for commit in "${COMMITS[@]}"; do
    MESSAGE=$(git log --format=%B -n 1 $commit)
    CLEAN_MESSAGE=$(echo "$MESSAGE" | sed '/🤖 Generated with/d; /Co-Authored-By: Claude/d' | sed -e :a -e '/^\n*$/{$d;N;ba' -e '}')

    echo "Cleaning commit $commit..."
    GIT_COMMITTER_DATE="$(git log -1 --format=%cD $commit)" \
    git commit --amend -m "$CLEAN_MESSAGE" --date="$(git log -1 --format=%aD $commit)" --no-edit
done

echo "Done! Now run: git push --force"
