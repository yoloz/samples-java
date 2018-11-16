package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtils {
	static String SYSTEM_CHARSETNAME = "utf-8";
	/**
	 * 基于注解的转换,只有注解的字段才会被转换
	 * @param obj
	 * @return
	 */
	public static String toJsonWithoutExposeAnnotation(Object obj){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(obj);
	}
	
	/**
	 * 转换所有字段为json格式
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	/**
	 * 将json转换为map
	 * @param json 字符串
	 * @return Map<String,String>
	 * @author lgg
	 * 206-08-12
	 */
	public static Map<String,String> parseToMap(String json){
		Type type = new TypeToken<Map<String,String>>() {}.getType();
        Gson gson = new Gson();
        Map<String,String> o = gson.fromJson(json, type);
		return o;
	}
	/**
	 * 将json转换为指定类
	 * @param json 字符串
	 * @return <T>
	 * @author lgg
	 * 206-08-12
	 */
	public static<T> T parse(String json,Class<T> cla){
		Gson gson = new Gson();
		T o = gson.fromJson(json, cla);
		return o;
	}
	/**
	 * List转化为JsonArray
	 * @param list
	 * @return
	 */
	public static JsonArray objToJsonArray(List list){
		String json = toJson(list);
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(json).getAsJsonArray();
		return jsonArray;
	}
	
	/**
	 * 转换所有字段为json格式
	 * @param obj
	 * @return
	 */
	public static String toJson2(String dataformat,Object obj){
		Gson gson = new GsonBuilder().setDateFormat(dataformat).create();
		return gson.toJson(obj);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Gson getGson() {
		Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class,
				new JsonDeserializer<HashMap>() {

					@SuppressWarnings("unused")
					@Override
					public HashMap deserialize(JsonElement arg0, Type arg1,
							JsonDeserializationContext arg2)
							throws JsonParseException {

						HashMap resultMap = new HashMap();
						JsonObject jsonObject = arg0.getAsJsonObject();
						Set<Map.Entry<String, JsonElement>> entrySet = jsonObject
								.entrySet();
						for (Map.Entry<String, JsonElement> entry : entrySet) {
							JsonElement value = entry.getValue();
							resultMap.put(entry.getKey(), entry.getValue());
						}
						return resultMap;
					}

				}).create();
		return gson;
	}
	/**
	 * 字符串转化为JsonObject
	 */
	public static JsonObject strToJsonObject(String str){
		JsonParser jp = new JsonParser();
        JsonObject d = jp.parse(str).getAsJsonObject();
        return d;
	}
	/**
	 * 字符串转化为JsonArray
	 */
	public static JsonArray strToJsonArray(String str){
		JsonParser jp = new JsonParser();
		JsonArray d = jp.parse(str).getAsJsonArray();
        return d;
	}
	
	
	/**
	 * json 对象保存到文件
	 * @param jsonFile
	 * @param pojo
	 * @throws IOException
	 */
	public static void writeObjToFile(File jsonFile, Object pojo) throws IOException {
		Gson g = new Gson();
		String json = g.toJson(pojo);
		writeStrToFile(jsonFile, json);
	}
	
	public static void writeStrToFile(File jsonFile, String json) throws IOException {
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "utf-8"));
		String str = jsonFormatter(json);
		fw.write(str);
		fw.flush();
		fw.close();
	}
	
	/**
	 * 从文件读取json对象
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static <T> T getObjFromFile(String path,  Class<T> clas) throws IOException {
		File file = new File(path);
		return getObjFromFile(file, clas);
	}
	
	/**
	 * 从文件读取json对象
	 * @param jsonFile
	 * @return
	 * @throws IOException
	 */
	public static <T> T getObjFromFile(File jsonFile, Class<T> clas) throws IOException{
		return getObjFromFile(jsonFile, clas, SYSTEM_CHARSETNAME);
	}
	
	public static <T> T getObjFromFile(File jsonFile, Type t) throws IOException{
		return getObjFromFile(jsonFile, t, SYSTEM_CHARSETNAME);
	}
	
	/**
	 * 从文件读取json对象
	 * @param jsonFile
	 * @param clas
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static <T> T getObjFromFile(File jsonFile, Class<T> clas, String encoding) throws IOException{
		String str = getStringFromFile(jsonFile, encoding);
		Gson g = new Gson();
		return (T)g.fromJson(str, clas);
	}
	
	public static <T> T getObjFromFile(File jsonFile, Type t, String encoding) throws IOException{
		String str = getStringFromFile(jsonFile, encoding);
		Gson g = new Gson();
		return g.fromJson(str, t);
	}
	
	public static String getStringFromFile(File jsonFile, String encoding) throws IOException{
		StringBuffer sb = new StringBuffer();
		
		BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), encoding));
		String str = fr.readLine();
		while (str != null) {
			sb.append(str);
			str = fr.readLine();
		}
		fr.close();
		
		return sb.toString();
	}
	
	public static String jsonFormatter (String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(json);
		String str = gson.toJson(je);
		return str;
	}
	public static void main(String[] args) {
	}
}
