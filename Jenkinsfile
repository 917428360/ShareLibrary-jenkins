#!groovy
@Library('devops-demo') _

def tools = new org.devops.tools()


//Getcode
String srcUrl = "${env.srcUrl}".trim()
String srcType = "${env.srcType}".trim()
String branchName = "${env.branchName}".trim()
String tagName = "${env.tagName}".trim()
String moduleName = "${env.moduleName}".trim()

//Global 
String workspace = "/var/lib/jenkins/workspace/${env.serviceName}"
String targetHosts = "${env.targetHosts}".trim()
String jobType = "${JOB_NAME}".split('_')[-1]
String credentialsId = "2ec7115a-bada-42bb-a3c4-bd9dd8a49af4"
String serviceName = "${env.serviceName}".trim()
String javaVersion = "${env.javaVersion}".trim()
String dependency = "${env.dependency}".trim()
String port = "${env.port}".trim()
String user = "${env.user}".trim()
String targetDir = "${env.targetDir}".trim()
def runserver 
def buildDir = tools.BuildDir(workspace,srcType,tagName,moduleName)[0]  
def srcDir = tools.BuildDir(workspace,srcType,tagName,moduleName)[1]  

//Build
String midwareType = "${env.midwareType}".trim()
String buildType = "${env.buildType}".trim()
String buildShell = "${env.buildShell}".trim()

//Pipeline

ansiColor('xterm') {
    node("master"){
        ws("${workspace}") {
            //Getcode
            stage("GetCode"){
                tools.PrintMes('获取代码','green')
                try {
                    def getcode = new org.devops.getcode()
                    getcode.GetCode(srcType,srcUrl,tagName,branchName,credentialsId)
                } catch(e){
                
                }    
            }
            
            //Build
            stage("RunBuild"){
                tools.PrintMes('应用打包','green')
                def build = new org.devops.build()
        
                try {
                    if ("${midwareType}" == "Nginx"){
                        build.WebBuild(srcDir,serviceName)
                    
                    } else if ("${midwareType}" == "NodeJs"){
                        def webDist=srcDir + '/dist'
                        sh " PATH=$PATH:/usr/local/node-v10.15.3-linux-x64/bin && cd ${srcDir} && ${buildShell} && cd -"
                        build.WebBuild(webDist,serviceName)
                    } else if ("${midwareType}" == "Tomcat"){
                        build.Build(javaVersion,buildType,buildDir,buildShell)
                    else {
                        build.Build(javaVersion,buildType,buildDir,buildShell)
                    }
                }catch(e){
                    currentBuild.description='运行打包失败！'
                    error '运行打包失败！'
                }
            }
            
            
            //Deploy
            stage("RunDeploy"){
                tools.PrintMes('发布应用','green')
                def deploy = new org.devops.deploy()
                
                switch("${midwareType}"){
                    case 'SpringBoot':
                        deploy.SpringBootInit(javaOption,dependency,credentialsId)
                        deploy.JavaDeploy('SpringBoot','jar',srcDir,user,targetHosts,targetDir+"/${serviceName}",port)
                        break;
                        
                    case 'Tomcat':
                        //def tomcatDir=targetDir + "/${port}/webapps/"
                        def tomcatDir=targetDir
                        deploy.JavaDeploy('Tomcat','war',srcDir,user,targetHosts,tomcatDir,port)
                        break;
                        
                    case 'NodeJs':
                        deploy.WebDeploy(user,serviceName,targetDir)
                        break;

                    case 'Nginx': 
                        deploy.WebDeploy(user,serviceName,targetDir)
                        break;

                    default:
                        error "中间件类型错误!"  
                }
            }
        }
    
    }
}
