def fileAdultReturn='/home/admin1/html_forms/adult_returnV5.html';
def filePMTCT='/home/admin1/html_forms/PMTCT_postnatal_v3.html';
def filePeadReturn='/home/admin1/html_forms/peadReturn.htm';
def fileCustom='/home/admin1/html_forms/customdata.html';
def fileContents = new File(fileCustom).getText('UTF-8')
def formData;
def obsMultiselect=[];
def multiselectMap=[];
def conceptMap=[];
def obsConceptGroupUuid='';
def form = new XmlSlurper().parseText(fileContents);
     println '{"formSchema":[';
      form.section.eachWithIndex{ section,index ->
           obsMultiselect=[];
           multiselectMap=[];
           section.attributes().each{k,obsSection->
               //println ' {"sectionLabel":"'+obsSection+'",'
               //println ' "sectionMembers":['
                     // Location
                    //if(form.section.lookup.size()>0){
                       //createLocationTag(section.lookup);
                     //}
                    // Single Observation

                    if(section.obs.size()>0){

                     createObs("",'""',section,section.obs,obsMultiselect,multiselectMap);
                     createMultiSelectObs(obsMultiselect,multiselectMap);
                     }
                  //Grouped Observations groups
                     if(section.obsgroup.size()>0){
                       obsMultiselect=[];
                       multiselectMap=[];
                         createGroupedObs(section.obsgroup,obsMultiselect,multiselectMap);
                     }
             }
            
         }
      println "]}";
  def createObs(groupConceptId,obsConceptGroupUuid,obsPart,path,obsMultiselect,multiselectMap){
  def ctnObs=0;
  def currentObsConceptId='';
  def checkBoxLabels='';
  def checkBoxOptions='';
  def obsFound=0;
  def multiSelect='';
  def allObs=obsPart.obs.size()
  def allHeaders= obsPart.h4.size() 

  path.eachWithIndex{obsNode,index->
     ctnObs++;
     def obsDelimiter='';
     def obsNodeMap=obsNode.attributes();
            if(obsNodeMap.style=='checkbox'){//getConceptUuid
             obsMultiselect.push(id:obsNodeMap.id,answerLabel:obsNodeMap.answerLabel,answerConceptId:obsNodeMap.answerConceptId,style:obsNodeMap.style,conceptId:obsNodeMap.conceptId
                       );
          }
             if(currentObsConceptId==obsNodeMap.conceptId){
               
                  //do nothing

             }else{
              
              if(obsNodeMap.style=='checkbox'){
                multiselectMap.push(obsNodeMap.conceptId)
                }

             }

           currentObsConceptId=obsNodeMap.conceptId
           ///print obs
            createObsTag(groupConceptId,obsConceptGroupUuid,path,obsNode,ctnObs,obsDelimiter)
  }

}


def createGroupedObs(path,obsMultiselect,multiselectMap){
  path.each{
       def obsGrps=it.attributes();
           obsMultiselect=[];
           multiselectMap=[];
                obsGrps.each{groupKey,groupConceptId->
               def obsConceptGroupUuid=getConceptUuid(groupConceptId)
               // println '{"obsConceptGroupUuid":"'+getConceptUuid(groupConceptId)+'",';
                //println '  "groupMembers":'
                // println '[';
                 //createQuestionLabel(it)
                 createObs(groupConceptId,obsConceptGroupUuid,it,it.obs,obsMultiselect,multiselectMap)
                 createMultiSelectObs(obsMultiselect,multiselectMap);
               // println "]";
                // println "}";
             }
     }
}

def createObsTag(GroupConceptId,obsConceptGroupUuid,path,obsNode,ctnObs,obsDelimiter){
//skip checkboxes
def obsAttrCount=0
def obsMap=obsNode.attributes();
def strQuotes='"';
def defaultType='';
 

if(obsMap.style!='checkbox'){
if(obsMap.labelText!=null && obsMap.answerConceptIds==null){
  defaultType='"type":"text",';
 }
 if(ctnObs<path.size()){
       obsDelimiter=','
 } 
     def groupIDS='';
    if(GroupConceptId)  groupIDS='"id_g":"'+GroupConceptId+'",';
         println '{"obsConceptGroupUuid":'+obsConceptGroupUuid+','+groupIDS;
         print defaultType;
          obsMap.each{ obsKey,obsVal->
            obsAttrCount++;
            def obsAttrDelimiter='';
            if(obsAttrCount<obsMap.size()){
              obsAttrDelimiter= ","
            }
                def questionConceptIdTag='';
                if(obsKey=='conceptId'){
                   questionConceptIdTag='"id":"'+obsVal+'",'+"\n";
                   obsVal=getConceptUuid(obsVal);
                   obsKey="obsConceptUuid";
                   //getAllConcepts(obsVal);
                   strQuotes='';
                }else{
                   if(obsVal){strQuotes='"';}
                }
                if(obsKey=='labelText')obsKey="label";
                if(obsKey=='id')obsKey='idHtml';
                def id_a='';
                if(obsKey=='answerConceptIds'){
                   obsKey="obsAnswerConceptUuids";
                   id_a='"id_a":"'+obsVal+"\",\n"+'"label":"p","type":"radio",'+"\n";
                   obsVal=getConceptUuidsFromList(obsVal)                   
                 } 
                 def startArray='', closeArray=''
                if((obsKey=="obsAnswerConceptUuids")||(obsKey=='answerLabels')){
                 startArray='[';
                 closeArray=']';
                 strQuotes='';
                };
               if(obsKey=='answerConceptId'){
                   obsKey="obsAnswerConceptUuids";
                   obsVal=getConceptUuidsFromList(obsVal);
                   strQuotes='';
                   //obsVal='"'+obsVal.replace(",","\",\"")+'"';
                 }

                if(obsKey=='answerLabels'){
                   obsKey="obsAnswerLabels";
                   strQuotes='"';
                   obsVal=addQuotesToLabels(obsVal)
                 }
                 if(obsKey=='answerLabel'){
                   obsKey="label";
                   //strQuotes='"';
                 }
   
                if(id_a)println id_a;
                if(questionConceptIdTag)println questionConceptIdTag;
                 if(obsVal.contains('"')){
                  strQuotes="";
                 }
                
          println '"'+obsKey+'":'+startArray+strQuotes+obsVal+strQuotes+closeArray+obsAttrDelimiter;
          strQuotes='';
         }
        println "}${obsDelimiter}"
 }
}

def createLocationTag(locNode){
def i=0, delimit='';
     locNode.each{ 
        n->
     i++;
    if(i<locNode.size()){
        delimit=','
      }
      
     println "{"; 
     def x=0,ld, latCtn=n.attributes().size();
 
        n.attributes().each{k,v->
             x++;
             ld='';
             if(x<latCtn){
              ld=',';
             }

             if(k=='style')k='type';
             if(k=='labelText')k='label';
             if(k=='id')k='idHtml';
             println '"'+k+'":"'+v+'"'+ld;
          }
             println "}"+delimit;
     }
}

def createMultiSelectObs(obsMultiselect,multiselectMap){
  multiselectMap.unique().each{questionConcept->
  print '{"obsConceptUuid":'+getConceptUuid(questionConcept)+",\n"+'"label":"p",'+"\n"+'"type":"multiCheckbox",'+"\n"+'"id":"'+questionConcept+'",'+"\n";
              createCheckboxOptions(obsMultiselect,questionConcept)
  println "},";
              
  }
}

def createCheckboxOptions(obsMultiselect,questionConcept){
      def obsItems=obsMultiselect.findAll{it.conceptId==questionConcept};
      def i=0,x,delimiter=',';
      def labels='',items='',itemsConcepts='';
      obsItems.each{ i++;
         if(i==obsItems.size())delimiter='';
             itemsConcepts=itemsConcepts + it.answerConceptId+delimiter;
             items=items + getConceptUuid(it.answerConceptId)+delimiter;
             labels=labels + it.answerLabel+delimiter;
      };
    // println '"id_a":"'+itemsConcepts+'",';
     println '"obsAnswerConceptUuids":['+items+'],';
     println '"obsAnswerLabels":['+addQuotesToLabels(labels)+']';   
}

def createQuestionLabel(node){
//println node.h4
}


def getConceptUuid(conceptId){
def conceptUUID='';
conceptId=conceptId.trim()
if(conceptId.isNumber()){
 admin.executeSQL(
   "select uuid from concept where concept_id=${conceptId}",
  true).each {row -> conceptUUID = row[0]}; 

}else{
println "NOT FOUND   XXXXXXXXXx====="+conceptId;
}
 return '"'+conceptUUID+'"';
 
}

def getConceptUuidsFromList(conceptStr){
def conceptUuids='';
conceptStr=conceptStr.replace(', ',',')
conceptStr=conceptStr.replace(',,',',')
def conceptsArray=conceptStr.split(',')
    conceptsArray.each{
         if(conceptUuids){
            conceptUuids=conceptUuids+','+getConceptUuid(it);
          }
          else{
           conceptUuids=getConceptUuid(it)
         }
      }
    return conceptUuids;
}

def addQuotesToLabels(inputStr){
def revisedStr='';
def strArray=inputStr.split(',')
    strArray.each{
         if(revisedStr){
            revisedStr=revisedStr+','+'"'+it+'"';
          }
          else{
           revisedStr='"'+it+'"'
         }
      }
    return revisedStr;
}
def getAllConcepts(conceptId){
  def conceptUUID='';
  //println conceptId;
  /*  admin.executeSQL(
  "select uuid from concept where concept_id=${conceptId}",
  true).each {row -> conceptUUID = row[0]};
  return conceptMap[conceptId]=conceptUUID;*/
////////////


////////////
}
​
