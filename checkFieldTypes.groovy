import groovy.json.JsonSlurper; 
import groovy.json.JsonBuilder; 
import java.util.List; 
import java.util.Map; 
def fileValidata="/home/admin1/html_forms/checkjsonform.json"
def fileContents = new File(fileValidata).getText('UTF-8')
def jsonSlurper = new JsonSlurper() 
def object = jsonSlurper.parseText(fileContents)  
JsonBuilder builder = new JsonBuilder( object)
 
object.schema.obs.each{it
println it.label;
  if(it.label==null){
println "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+it.id
   }

println it.type;
  if(it.type==null){
println "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT==="+it.id
   }

}
​
