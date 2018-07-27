/*
*The script creates project and associated workspaces and kicks off the scan with the default settings for all of them
*based on two files that were created previously
 */


import com.palamida.appcore.properties.ApplicationProperties
import com.palamida.script.*
import com.palamida.script.WorkspaceLocatorCover
import java.util.ArrayList;  
import java.util.List;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.FileWriter;
import java.io.File.*
import groovy.xml.*

String hostName = "http://localhost:8888/";
String httpPort = "8888";
String serverUri = "http://$hostName:$httpPort/";
String description = "improted from 6.14"
String ownerLogin = "user"
String scannerAlias = "LocalScanner"

locator.setServerUri(hostName);

def coreServer = ApplicationProperties.getInstance().getCoreServer()
def adminSrv = new AdminServiceCover(coreServer)
def projSrv = new ProjectDataCover(coreServer)

def user = adminSrv.getUser("epakhomov")
//println([user.getFirstName(), user.getLastName()].join(" "))
def teamName = "The Best Team"

//Location of two files
String source = new File("output1.xml") 
String dest = new File("otput1mod.xml")
String source2 = new File("output2.xml")
String dest2 = new File("output2mod.xml")

// Code that removes blank space and brackets from the files

new File(dest).withWriter { w ->
new File(source).eachLine { line ->
    w << line.replaceAll("\\s","",).replaceAll("\\[", "").replaceAll("\\]","")
  }
}

new File(dest2).withWriter { w ->
new File(source2).eachLine { line ->
    w << line.replaceAll("\\s","",)
  }
}


//Initiating Xml parser
def root = new XmlSlurper().parse(new File(dest))
def root2 = new XmlSlurper().parse(new File(dest2))

def wrkList =[]
def wrkList2 =[]
def pathList = []
def prList = []
def myMap = [:]
def myMap1 = [:]
def newList = []
def newList2 = []

//reading xml and writing values to various lists
root.project.each{
prList.add(it.text())
}

root.workspace.each{
wrkList.add(it.text())
}

root2.path.each{
pathList.add(it.text())
}

root2.worksapce.each{
wrkList2.add(it.text())
}
//splitting string in list and putting it to another list
wrkList.each { w->
newList.add(w.tokenize(','))
}
//iterators to create maps
Iterator<String> i1 = prList.iterator();
Iterator<String> i2 = newList.iterator();

while (i1.hasNext() && i2.hasNext()) {
    myMap.put(i1.next(),i2.next());
}
//creating project and workspaces
myMap.each {p, w ->
def projectId = adminSrv.createOrUpdateProject(p, description, teamName, ownerLogin)
w.each { ww -> 
def success = projSrv.createWorkspace(teamName, p, ww, scannerAlias)
}

}

pathList.each { pa->
newList2.add(pa.tokenize(','))
}

Iterator<String> i3 = wrkList2.iterator();
Iterator<String> i4 = newList2.iterator();

while (i3.hasNext() && i4.hasNext()) {
    myMap1.put(i3.next(),i4.next());
}



def urilist = []
 wrkList2.each { w ->
        String wsName = w 
        String wsUri = hostName + w;
        urilist.add(wsUri)
}

def map2 = [:]
for (int i = 0; i < urilist.size(); i++) {
  map2.put(urilist.get(i), newList2.get(i));
}

map2.each {k,v->

        def ws;
        try 
        {
        ws = locator.openWorkspace(k)
def filePaths = v as Set

println filePaths
def fileset = locator.createFileset()
fileset.setPaths(filePaths)
ws.updateScanFileSet(fileset)

        } finally {
    if (ws)
        ws.close()
    locator.destroy()
}
}

//kicking off scans
/*def scannerUri = "http://localhost:8888/palamidaScanEngine"
def schedSrv = locator.getSchedulerService(scannerUri)


wrkList2.each { wr ->
def uri = [scannerUri, wr].join("/")
println uri
def task = schedSrv.addScanTask(uri)
}*/
