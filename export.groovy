import com.palamida.appcore.properties.ApplicationProperties
import com.palamida.script.*
import com.palamida.script.WorkspaceLocatorCover
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileWriter;
import groovy.xml.*

String hostName = "http://127.0.0.1/";
def coreServer = ApplicationProperties.getInstance().getCoreServer()
def adminSrv = new AdminServiceCover(coreServer)
def projSrv = new ProjectDataCover(coreServer)
def teamName = "The Best Team"

String path1, path2

def fileWriter = new FileWriter(path1)
def fileWriter1 = new FileWriter(path2)

def myMap = [:]
def myMap0 = [:]

List projects = adminSrv.getProjectNames(teamName)

//Getting information about project and workspaces and putting it to a map

projects.each {
  projectName -> myMap0.put (projectName,projSrv.getWorkspaceNamesForProject(teamName, projectName))
}

//Writing the map to the first file
def xml = new MarkupBuilder(fileWriter)
xml.root (){
            myMap0.each { k, v ->
            project (k)
	    workspace (v)
            }
        }
//Getting information about workspaces and their scanpaths and putting to another map
projects.each { p->
 List<Map<String,String>> workspaces = adminSrv.getWorkspaceNamesForProject(teamName, p);
   workspaces.each { w ->
        String wsName = w
        String wsUri = hostName + w;
        def ws;
        try
        {
            ws = locator.openWorkspace(wsUri)
            String scanPaths = "";
            ws.getScanFileSet().getPaths().each
            { fp ->
                if (scanPaths.equals(""))
                {
                    scanPaths = fp;
                }
	         else {
		scanPaths = scanPaths + ", " + fp;
							}
            }
            myMap.put(wsName, scanPaths)

        } finally {
    if (ws)
        ws.close()
    locator.destroy()
}
                        }
                        }
//Writing the second map to the second file
def xml1 = new MarkupBuilder(fileWriter1)

 xml1.root (){
      myMap.each { k, v ->
      worksapce (k)
      path(v)
            }
        }

