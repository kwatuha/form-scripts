def getConceptUuid(conceptId){
def conceptUUID='';
def conceptID='';
def conceptLabels='';

def where=" where concept.concept_id in (select answer_concept from concept_answer where concept_id=${conceptId})"
def   sql=" select concept_name.name,concept.uuid,concept.concept_id  from  concept " + 
        ' inner join concept_name on concept_name.concept_id=concept.concept_id ' +where;

admin.executeSQL(sql,true).each {row ->
conceptLabels=conceptLabels+'"'+row[0]+'",';
conceptUUID=conceptUUID+'"'+row[1]+'",';
conceptID=conceptID+'"'+row[2]+'",';
};
conceptUUID='['+conceptUUID+']';
conceptLabels='['+conceptLabels+']';
conceptID='['+conceptID+']';

println '"obsAnswerConceptUuids":'+conceptUUID+',';
println '"id_a":'+conceptID+',';
println '"obsAnswerLabels":'+conceptLabels+',';


}
getConceptUuid(1668);​
