/**
 * Checks out source code from a Git repository.
 *
 * @param Map params
 *   - url           : Git repository URL (required)
 *   - branch        : Git branch to check out (default: 'main')
 *   - credentialsId : Jenkins credential ID for Git (optional)
 */
def call(Map params = [:]) {
    checkout([
        $class: 'GitSCM',
        branches: [[ name: params.branch ?: 'main' ]],
        userRemoteConfigs: [[
            url           : params.url,
            credentialsId : params.credentialsId ?: ''
        ]]
    ])
}
