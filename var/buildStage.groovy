/**
 * Builds a Maven project.
 *
 * @param Map params
 *   - pomPath : Path to the pom.xml (default: 'pom.xml')
 *   - goals   : Maven goals to run (default: 'clean package')
 *   - opts    : Additional Maven options (optional)
 */
def call(Map params = [:]) {
    def pom    = params.pomPath ?: 'pom.xml'
    def goals  = params.goals   ?: 'clean package'
    def extras = params.opts    ?: ''
    sh "mvn -f ${pom} ${goals} ${extras}".trim()
}
