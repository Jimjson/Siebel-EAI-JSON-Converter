import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessService;
import com.siebel.eai.SiebelBusinessServiceException;
import com.google.gson.*;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;

/*
Copyright (c) 2013 howtosiebel.blogspot.com
Authors: Jim Morse

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class EAIJSONConverter extends com.siebel.eai.SiebelBusinessService {

public void doInvokeMethod(String methodName, SiebelPropertySet input,SiebelPropertySet output) throws SiebelBusinessServiceException{

	if(methodName.equals("PropSetToJSON")){
		JsonObject myJSON = new JsonObject();
		myJSON = traversePS(input,myJSON);
		output.setValue(myJSON.toString());
	}


	if(methodName.equals("JSONToPropSet")){
		JsonObject obj = new JsonObject();
		obj = new Gson().fromJson(input.getValue(), JsonObject.class);
		SiebelPropertySet op = new SiebelPropertySet();
		output.addChild(JsonObjectToPropertySet(obj,op));
	}	

}

public SiebelPropertySet temp(Map MapObject){
	Iterator iterator = MapObject.entrySet().iterator();
	SiebelPropertySet op = new SiebelPropertySet();
	SiebelPropertySet psw = new SiebelPropertySet();
	String str2 = "something";

	while (iterator.hasNext()) {
		Map.Entry mapEntry = (Map.Entry) iterator.next();
		Object obj2 = mapEntry.getValue();
		if(obj2 != null){
		if(obj2 instanceof float[]){
			str2 = "array found";		
		}
			
		if(obj2 instanceof Map){
			psw = temp((Map)obj2);
			psw.setType(mapEntry.getKey().toString());
			op.addChild(psw);
		}
		else 
			op.setProperty(mapEntry.getKey().toString(),mapEntry.getValue().toString()); 
		}
	}
	op.setValue(str2);
	return op;
}

public static SiebelPropertySet JsonObjectToPropertySet(JsonObject obj, SiebelPropertySet ps){
		Iterator<Entry<String, JsonElement>> iterator = obj.entrySet().iterator();
			SiebelPropertySet child;
			while (iterator.hasNext()) {
				JsonArray jsonArray = new JsonArray(); 
				JsonObject jsonObject = new JsonObject();
				Map.Entry mapEntry = (Map.Entry) iterator.next();
				if(mapEntry!=null){
				JsonElement jsonelement = (JsonElement) mapEntry.getValue();
				if(jsonelement.isJsonArray()){
					jsonArray = jsonelement.getAsJsonArray();
					child = new SiebelPropertySet();
					child.setType("ListOf-" + mapEntry.getKey().toString());
					for(int i=0; i<jsonArray.size();i++){
						if(jsonArray.get(i).isJsonObject() || jsonArray.get(i).isJsonArray()){
							SiebelPropertySet temp = new SiebelPropertySet();
							temp.setType("" + i);
							if (jsonArray.get(i).isJsonObject())
							  child.addChild(JsonObjectToPropertySet(jsonArray.get(i).getAsJsonObject(),temp));
							else 
							{	
								JsonObject aux= new JsonObject();
								aux.add(""+i, jsonArray.get(i));
								child.addChild(JsonObjectToPropertySet(aux,temp));
									
							}
						}
						else
							child.setProperty("" + i, jsonArray.get(i).getAsString());
					}
					ps.addChild(child);	
				}else if(jsonelement.isJsonObject()){
					jsonObject = jsonelement.getAsJsonObject();
					child = new SiebelPropertySet();
					child.setType(mapEntry.getKey().toString());
					ps.addChild(JsonObjectToPropertySet(jsonObject, child));
				} else{
					ps.setProperty(mapEntry.getKey().toString(),mapEntry.getValue().toString());
				}
				}
			}
			
		return ps;
		}



public JsonObject traversePS(SiebelPropertySet ps,JsonObject jObj){
	JsonObject siebJSON = new JsonObject();
	String propName;
	String propVal;
	propName = ps.getFirstProperty();

	while (propName != "") {
		propVal = ps.getProperty(propName);
		siebJSON.addProperty(propName,propVal);
		propName = ps.getNextProperty();
	}

	JsonObject child;
	
	for (int i = 0; i < ps.getChildCount(); i++){
		child = new JsonObject();


		if (ps.getChild(i).getType().startsWith("ListOf-"))
			
		{	JsonArray ja=new JsonArray();
			for (int o=0 ;o<ps.getChild(i).getChildCount();o++)
			{ 
			      ja.add(traversePS(ps.getChild(i).getChild(o),child));
			} 
			siebJSON.add(ps.getChild(i).getType().substring(7),ja);
		}
		else if (ps.getChild(i).getType().startsWith("StringList-"))

		{	JsonArray ja=new JsonArray();
            		SiebelPropertySet auxps=ps.getChild(i);
			Enumeration e = auxps.getPropertyNames();
            		List list = Collections.list(e);
            		Collections.sort(list);
            		e=Collections.enumeration(list) ;

			while (e.hasMoreElements()) {
                		String propName1=(String) e.nextElement();
				ja.add(new JsonPrimitive(auxps.getProperty(propName1)));
            		}
			siebJSON.add(auxps.getType().substring(11),ja);
		}
		else 
		{	
			child = traversePS(ps.getChild(i),child);
  	     		siebJSON.add(ps.getChild(i).getType(),child);
		}
	}



	return siebJSON;
}

}
}
