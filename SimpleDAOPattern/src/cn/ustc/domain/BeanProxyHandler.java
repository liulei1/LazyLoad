package cn.ustc.domain;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import cn.ustc.DAO.Conversation;

public class BeanProxyHandler implements InvocationHandler {
	private Object obj;
	@SuppressWarnings("rawtypes")
	private Conversation conversion;
	@SuppressWarnings("rawtypes")
	public BeanProxyHandler(Object obj, Conversation conversion) {
		super();
		this.obj = obj;
		this.conversion = conversion;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Map<String, Object> beanMap = conversion.getBeanMap();
		String methodName = method.getName();
		String fieldName = Character.toLowerCase( methodName.charAt(3)) + methodName.substring(4, methodName.length());
		Map<String,Object> propertyMap = (Map<String, Object>) beanMap.get("properties");
		try {
			Map<String, String> fieldMap = (Map<String, String>) propertyMap.get(fieldName);
			
			if(fieldName.equals("tring")){
				method.invoke(obj, args);
			}
			
			String lazy = (String) fieldMap.get("lazy");
//			String type = (String) fieldMap.get("type");
			String column = (String) fieldMap.get("column");
			Object value = method.invoke(obj, args);
			
			if(value == null){
				if("false".equals(lazy)){
					// 不是懒加载
					return value;
				}else {
					// 懒加载， 查询数据库
					String idName = (String) beanMap.get("id");
					Field idField = obj.getClass().getDeclaredField(idName);
					idField.setAccessible(true);
					Object id =  idField.get(obj);
					
					Field lazyField = obj.getClass().getDeclaredField(fieldName);
					lazyField.setAccessible(true);
					
					Object newInstance = obj.getClass().newInstance();
					// 查询数据库
					newInstance = conversion.getProperty(column, fieldName, id);
					Field field = newInstance.getClass().getDeclaredField(fieldName);
					field.setAccessible(true);
					value = field.get(newInstance);
					lazyField.set(obj, value); //懒加载值加入对象
					return value;
				}
				
			}else{
				return method.invoke(obj, args);
			}
		} catch (Exception e) {}
		
		return method.invoke(obj, args);
	}

}
