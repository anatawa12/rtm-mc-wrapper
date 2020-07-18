# utilities
function get_latest_release() {
  curl -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/$1/releases/latest" |
    jq -r '.tag_name'
}

# 仮ファイルの準備

function rm_tmpfile {
  [[ -f "$1" ]] && rm -f "$1"
}

function make_temp() {
  local tmp_file_1
  tmp_file_1=$(mktemp)

  # shellcheck disable=SC2064
  trap "rm_tmpfile '$tmp_file_1'" EXIT
  # shellcheck disable=SC2064
  trap "trap - EXIT; rm_tmpfile '$tmp_file_1'; exit -1" INT PIPE TERM

  echo "$tmp_file_1"
}

tmp_file=$(make_temp)

# バージョン情報を作成
if [ "$VERSION_NAME_IN" == "snapshot-generated" ]; then
    version_name="$(date "+SNAPSHOT-%Y-%m-%d-%H-%M-%S")"
    prerelease=true
else
    version_name="$VERSION_NAME_IN"
    prerelease=false
fi

# リリース情報を追加

release_note_path=$(mktemp)

if [ "$prerelease" != "true" ]; then
  latest_release="$(get_latest_release "$GITHUB_REPOSITORY")"

  curl -H "Authorization: token $GITHUB_TOKEN" \
      "https://api.github.com/repos/$GITHUB_REPOSITORY/pulls?state=closed" > "$tmp_file"

  commit_tmp=$(make_temp)

  echo "latest_release: $latest_release"
  # get commits
  curl -H "Authorization: token $GITHUB_TOKEN" \
      "https://api.github.com/repos/$GITHUB_REPOSITORY/compare/$latest_release...$GITHUB_SHA" \
      | jq '.commits[] | .sha' -r \
      | cut -d' ' -f1 | while read -r merge_commit_sha; do
    echo "merge_commit_sha: $merge_commit_sha"
    jq ".[] | select(.merge_commit_sha == \"$merge_commit_sha\")" < "$tmp_file" > "$commit_tmp"

    relates_issues="$(cat "$commit_tmp" | jq ".body" -r \
        | grep -oiE '((close|resolve)(|s|d)|(fix)(|es|ed))\s+(([a-z0-9-]+/[a-z0-9-]+)?#[0-9]+)' \
        | grep -oE '#[0-9]+' \
        | tr '\n' ' ')"

    merge_request_number="$(jq ".number" < "$commit_tmp")"
    echo "- $relates_issues (merge request #$merge_request_number)" >> "$release_note_path"
  done

else
  echo "" > "$release_note_path"
fi


# gradle.properties を更新
cat gradle.properties > "$tmp_file"
sed 's/^rtmMcWrapperVersion=.*/rtmMcWrapperVersion='"$version_name"'/' "$tmp_file" > gradle.properties
rm "$tmp_file"

# コミット

if [ "$prerelease" != "true" ]; then
  git commit -am "$version_name"
fi
git tag "$version_name"
git push
git push origin "$version_name"

# 出力設定

asset_path="./build/libs/rtm-mc-wrapper-$version_name.zip"
asset_name="rtm-mc-wrapper-$version_name.zip"

echo "::set-output name=release_note_path::$release_note_path"
echo "::set-output name=version_name::$version_name"
echo "::set-output name=prerelease::$prerelease"
echo "::set-output name=asset_path::$asset_path"
echo "::set-output name=asset_name::$asset_name"
