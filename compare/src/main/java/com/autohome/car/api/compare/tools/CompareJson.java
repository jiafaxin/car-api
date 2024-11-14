package com.autohome.car.api.compare.tools;

import com.autohome.car.api.common.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompareJson {

    List<String> errors = new ArrayList<>();


    List<String> exFields = new ArrayList<>();

    public static Map<String, String> map = new HashMap<>();

    static {
        map.put("test", "http://car-api.mesh-thallo.corpautohome.com");
        map.put("beta", "http://car-car-api-test.thallo.corpautohome.com");
        map.put("yzpro", "http://10.180.218.64");
        map.put("car0", "http://car0.api.autohome.com.cn");
        map.put("dev", "http://localhost:8086");
    }

    public CompareJson exclude(String... exfs){
        exFields = new ArrayList<>();
        if (exfs == null) {
            return this;
        }
        for (String exf : exfs) {
            exFields.add(exf);
        }
        return this;
    }

    public void compareUrl(String url1, String url2) {
        compareUrlAsync(url1,url2).join();
    }


    final static String onlineDomain = "http://car.api.autohome.com.cn";
    //final static String onlineDomain = "http://caraddl.api.autohome.com.cn";
//    final static String betaDomain = "http://10.180.218.64";
    final static String betaDomain = "http://car-api.mesh-thallo.corpautohome.com";

//    final static String onlineDomain = "http://localhost:30185";
//    final static String betaDomain = "http://localhost:8086";

    @Deprecated
    public CompletableFuture compareUrlAsyncCommon(String url){
        return compareUrlAsync(onlineDomain.concat(url), betaDomain.concat(url),"","car.api.autohome.com.cn");
    }

    public CompletableFuture compareUrlAsyncCommon(String url, String evn){
        return compareUrlAsync(onlineDomain.concat(url), map.get(evn).concat(url),"","");
    }

    public CompletableFuture compareUrlAsync(String url1, String url2){
        return compareUrlAsync(url1,url2,"","");
    }

    public CompletableFuture compareUrlAsync(String url1, String url2,String host1,String host2) {
        return HttpClient.getString(url1, "UTF-8",host1).thenCombineAsync(HttpClient.getString(url2, "UTF-8",host2), (a, b) -> {
            try {
                compare(a, b);
            }catch (Exception e){
                System.out.println(e);
                System.out.println(url1+ " >>> " +url2);
                return null;
            }

            if(errors.size()>0){
                System.out.println("==================================================================================================================");
                System.out.println("A: "+url1);
                System.out.println("B: "+url2);

                for (String error : errors) {
                    System.out.println(error);
                }

            }
            return null;
        }).exceptionally(e->{
            System.out.println("exceptionally:"+  e.toString());
            System.out.println("error:"+ url2);
            return null;
        });
    }


    public void compare(String oldJson,String newJson){
        JSONObject oldObj = new JSONObject(oldJson);
        JSONObject newObj = new JSONObject(newJson);

        compare("root",oldObj,newObj);
    }

    public void compare(String key, Object oldObj,Object newObj) {
        if(exFields.contains(key) || exFields.contains(key.replaceAll("\\[\\d+\\]","[*]")))
            return;

        if (oldObj == null && newObj == null) {
            return;
        }

        if (oldObj.equals(JSONObject.NULL) == true && newObj.equals(JSONObject.NULL) == true) {
            return;
        }

        if (oldObj.equals(JSONObject.NULL) && !newObj.equals(JSONObject.NULL)) {
            if(newObj instanceof  JSONArray){
                if(((JSONArray)newObj).length()==0){
                    return;
                }
            }

            if(newObj instanceof String){
                if(((String)newObj).equals("")){
                    return;
                }
            }

            errors.add(key + " : A 结果为null，B 结果不为null");
            return;
        }
        if ((!oldObj.equals(JSONObject.NULL)) && (newObj.equals(JSONObject.NULL) || newObj.equals(JSONObject.NULL) == true)) {
            errors.add(key + " : A 结果不为null，B 结果为null");
            return;
        }

        if (oldObj instanceof JSONArray) {
            if (!(newObj instanceof JSONArray)) {
                errors.add(key + " : A 为JSONArray，B 不是");
            }
            JSONArray oldArray = (JSONArray) oldObj;
            JSONArray newArray = (JSONArray) newObj;
            int oldLen = oldArray.length();
            for (int i = 0; i < oldLen; i++) {
                //过滤数组中的null
                if (oldArray.isNull(i)) {
                    i--;
                    oldLen--;
                    continue;
                }
                if (newArray.length() < i + 1) {
                    errors.add(key + "[" + i + "] A 存在，B 不存在");
                    continue;
                }
                compare(key + "[" + i + "]", oldArray.get(i), newArray.get(i));
            }

            return;
        }
        if (oldObj instanceof JSONObject) {
            if (!(newObj instanceof JSONObject)) {
                errors.add(key + " : A 为JSONObject，B 不是");
            }

            JSONObject oldObjC = (JSONObject) oldObj;
            JSONObject newObjC = (JSONObject) newObj;

            for (String s : oldObjC.keySet()) {
                String newKey = key + "." + s;
                if (!newObjC.keySet().contains(s)) {
                    errors.add(newKey + " : A 存在，B 不存在");
                    continue;
                }
                Object oldValue = oldObjC.get(s);
                Object newValue = newObjC.get(s);
                compare(newKey, oldValue, newValue);
            }
            return;
        }
        if (oldObj.equals(newObj)) {
            return;
        }else if(oldObj.equals(null) && newObj.equals("")){
            return;
        }
        else if(isDouble(oldObj) && isDouble(newObj) && Double.parseDouble(oldObj.toString()) == Double.parseDouble(newObj.toString())){
            return;
        }
        errors.add(key + ": A、B值不一致 [" + oldObj.toString() + "] => [" + newObj.toString()+"]");
    }

    public boolean isDouble(Object v){
        try{
            Double.parseDouble(v.toString());
            return true;
        }
        catch (NumberFormatException ee){
            return false;
        }
        catch (Exception e){
            return false;
        }
    }

}
