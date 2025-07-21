/**
 * Runs unit tests for a Maven project.
 *
 * @param Map params
 *   - pomPath : Path to the pom.xml (default: 'pom.xml')
 */
def call(Map params = [:]) {
    def pom = params.pomPath ?: 'pom.xml'
    sh "mvn -f ${pom} test"
}
