/**
 * Deploys a WAR file to a Tomcat server.
 *
 * @param Map params
 *   - warPath      : Path to the WAR file (required)
 *   - url          : Tomcat Manager base URL (required)
 *   - credentialsId: Jenkins credential ID for Tomcat creds (optional)
 *   - context      : Context path (default: '/')
 */
def call(Map params = [:]) {
    def context = params.context ?: '/'
    if (params.credentialsId) {
        withCredentials([usernamePassword(
            credentialsId: params.credentialsId,
            usernameVariable: 'USER',
            passwordVariable: 'PASS'
        )]) {
            sh """
                curl -u ${USER}:${PASS} \
                     --upload-file ${params.warPath} \
                     "${params.url}/manager/text/deploy?path=${context}&update=true"
            """
        }
    } else {
        sh """
            curl -u ${params.user}:${params.pass} \
                 --upload-file ${params.warPath} \
                 "${params.url}/manager/text/deploy?path=${context}&update=true"
        """
    }
}
