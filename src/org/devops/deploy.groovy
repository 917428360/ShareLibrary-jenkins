package org.devops


//saltapi模板
def Salt(salthost,saltfunc,saltargs) {
    result = salt(authtype: 'pam', 
                clientInterface: local( arguments: saltargs,
                                        function: saltfunc, 
                                        target: salthost, 
                                        targettype: 'list'),
                credentialsId: "c4ec3410-7f97-40fa-8ad9-be38a7bbbcd8", 
                servername: "http://127.0.0.1:8000")
    println(result)
    //PrintMes(result,'blue')
    return  result
}


//前端类型发布
def WebDeploy(user,serviceName,targetDir){
    try {
        println('清空发布目录')
        try {
            Salt(targetHosts,'cmd.run', "cmd=\" rm -fr  ${targetDir}/* \"")
        } catch(e) {
            println('delete')
        }
        println('发布软件包')
        Salt(targetHosts,'cp.get_file', "salt://${JOB_NAME}/${serviceName}.tar.gz ${targetDir}/${serviceName}.tar.gz makedirs=True ")
        sleep 2;
        
        println('解压')
        Salt(targetHosts,'cmd.run', "cmd=\" cd ${targetDir} && tar zxf ${serviceName}.tar.gz  \"")
        sleep 2;
        
        println('授权')
        Salt(targetHosts,'cmd.run', "cmd=\"chown ${user}:${user} ${targetDir} -R  \"")
        sleep 2;
        println('获取发布文件')
        Salt(targetHosts,'cmd.run', "cmd=\" ls -l  ${targetDir} \"")
        
        println('删除缓存文件')
        sh "rm -fr /srv/salt/${JOB_NAME}/*"
    } catch (e){
        currentBuild.description='包发布失败！'
        error '包发布失败！'
    }
}

