#!/usr/bin/env -S jq -nf
{
  "ignorePatterns": [
    {"pattern": "^https?://localhost"},
    {"pattern": "^https?://example"},
    {"pattern": "^https://checkstyle\\.sourceforge\\.io"},
    {"pattern": "^https://www\\.linkedin\\.com"},
    {"pattern": "https://(.*?)\\.azure\\.sovity\\.io"},
    {"pattern": "http://edc[0-9]*:"},
    {"pattern": "https://test-connector.hosting-environment.io"},
    {"pattern": "https://github.com/sovity/dataspace-portal-ee"}
  ],
  "replacementPatterns": [
    {
      "pattern": "^\(
               env | .GITHUB_SERVER_URL // ("GITHUB_SERVER_URL was null" | halt_error)
           )/\(
               env | .GITHUB_REPOSITORY // ("GITHUB_REPOSITORY was null" | halt_error)
           )/blob/main/",
      "replacement": "\(
            env | .GITHUB_SERVER_URL // ("GITHUB_SERVER_URL was null" | halt_error)
        )/\(
            env | .GITHUB_REPOSITORY // ("GITHUB_REPOSITORY was null" | halt_error)
        )/blob/\(
            env | .CI_SHA // ("CI_SHA was null" | halt_error)
        )/"
    },
    {
      "pattern": "^\(
               env | .GITHUB_SERVER_URL // ("GITHUB_SERVER_URL was null" | halt_error)
           )/\(
               env | .GITHUB_REPOSITORY // ("GITHUB_REPOSITORY was null" | halt_error)
           )/tree/main/",
      "replacement": "\(
            env | .GITHUB_SERVER_URL // ("GITHUB_SERVER_URL was null" | halt_error)
        )/\(
            env | .GITHUB_REPOSITORY // ("GITHUB_REPOSITORY was null" | halt_error)
        )/tree/\(
            env | .CI_SHA // ("CI_SHA was null" | halt_error)
        )/"
    }
  ]
}
