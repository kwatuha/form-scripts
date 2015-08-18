import groovy.json.JsonSlurper; 
import groovy.json.JsonBuilder; 
import java.util.List; 
import java.util.Map; 
def fileValidata="/home/admin1/angularDev/ng-amrs-folk/app/scripts/formentry/formschema/form1.json"
def fileContents = new File(fileValidata).getText('UTF-8')
def jsonSlurper = new JsonSlurper() 
def object = jsonSlurper.parseText(fileContents)  
def JsonBuilder builder = new JsonBuilder( object)

object.schema.obs.each{it
  if(it.obsAnswerConceptUuids){
     it.answers= formatOptions(it.obsAnswerConceptUuids, it.obsAnswerLabels);     
   }

}


def formatOptions(answerOptions, labelOptions){
 def conceptAnswer="";
    if( answerOptions.size() == labelOptions.size()){
      println "UUIDs" + answerOptions
      println "Labels" + labelOptions      
       answerOptions.eachWithIndex{option,index->
         conceptAnswer+='{"concept": "'+option+'", "label": "'+labelOptions[index]+'"},'+"\n";
       }
   }

return '['+"\n"+conceptAnswer+'],';
}
writeToFile(object.schema.obs); 
def writeToFile(def infoList) { 
def directory="/home/admin1/html_forms/";
new File("${directory}formAlpha.json").withWriter { 
       out ->
             infoList.each { 
               out.println '{'
               out.println '"obsConceptGroupUuid":"'+it.obsConceptGroupUuid+'",'
               out.println '"obsConceptUuid":"'+it.obsConceptUuid+'",'
                 if(it.label)
               out.println '"label":"'+it.label+'",';
               if(it.answers)
               out.println '"answers":'+it.answers;
               if(it.type)
               out.println '"type":"'+it.type+'",';

               out.println '"validators":"'+it.validators+'"';
               out.println '},'
            }   
}
}
​
