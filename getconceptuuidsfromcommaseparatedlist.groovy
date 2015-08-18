def getConceptUuid(conceptIds){
def conceptUUID='';

def m=conceptIds.split(',');
m.each{
if(it!=null){conceptUUID=conceptUUID+getConceptSingleUuid(it.trim())}
}
conceptUUID='['+conceptUUID+']';
println '"obsAnswerConceptUuids":'+conceptUUID+',';


}

def getConceptSingleUuid(conceptId){
def conceptUUID='';
def sql=" select  uuid  from  concept where concept_id =(${conceptId}) " 
      def rows= admin.executeSQL(sql,true).each {row ->conceptUUID=conceptUUID+'"'+row[0]+'",';}
      if(rows.size()<=0){ conceptUUID='"NOTFOUND '+conceptId+'",' }
     
  return conceptUUID;
}
getConceptUuid("1171,212160009, 1170,2167");​
