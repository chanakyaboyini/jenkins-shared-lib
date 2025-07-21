// vars/launchEC2Instance.groovy

/**
 * Launches a new EC2 instance and sets env.INSTANCE_ID
 *
 * @param Map params (all optional—defaults read from env)
 *   - credentialsId : Jenkins AWS creds ID (defaults to env.AWS_CREDENTIALS)
 *   - imageId       : AMI ID (defaults to env.AMI_ID)
 *   - instanceType  : EC2 instance type (defaults to env.INSTANCE_TYPE)
 *   - keyName       : SSH key name (defaults to env.KEY_NAME)
 *   - securityGroup : Security Group ID (defaults to env.SECURITY_GROUP)
 *   - subnetId      : Subnet ID (defaults to env.SUBNET_ID)
 *   - region        : AWS region (defaults to env.AWS_REGION)
 */
def call(Map params = [:]) {
    // pick up values from params or fallback to environment
    String creds   = params.credentialsId ?: env.AWS_CREDENTIALS
    String image   = params.imageId       ?: env.AMI_ID
    String type    = params.instanceType  ?: env.INSTANCE_TYPE
    String key     = params.keyName       ?: env.KEY_NAME
    String sg      = params.securityGroup ?: env.SECURITY_GROUP
    String subnet  = params.subnetId      ?: env.SUBNET_ID
    String region  = params.region        ?: env.AWS_REGION

    withCredentials([[
      $class: 'AmazonWebServicesCredentialsBinding',
      credentialsId: creds,
      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
    ]]) {
        // run aws cli to launch instance
        def id = sh(
          script: """
            aws ec2 run-instances \\
              --image-id ${image} \\
              --count 1 \\
              --instance-type ${type} \\
              --key-name ${key} \\
              --security-group-ids ${sg} \\
              --subnet-id ${subnet} \\
              --region ${region} \\
              --query 'Instances[0].InstanceId' \\
              --output text
          """.stripIndent(),
          returnStdout: true
        ).trim()

        // export to env for downstream stages
        env.INSTANCE_ID = id
        echo "Launched EC2 instance: ${id}"
    }
}
