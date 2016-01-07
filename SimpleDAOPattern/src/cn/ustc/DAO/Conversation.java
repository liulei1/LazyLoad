package cn.ustc.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.ustc.domain.BeanProxyHandler;
import cn.ustc.domain.Configuration;
import cn.ustc.domain.User;

public class Conversation<T> {
	private T instance;
	@SuppressWarnings("unchecked")
	private static Map<String, Object> clazzMap = (Map<String, Object>) Configuration.xmlMap.get("class");
	private Map<String, Object> beanMap;// 类的映射信息
	private String table; // 数据表名
	
	@SuppressWarnings("unchecked")
	public Conversation(T instance) {
		super();
		this.instance = instance;
		this.beanMap = (Map<String, Object>) clazzMap.get(instance.getClass().getName());
		this.table = (String) beanMap.get("table");
	}
	
	public Map<String, Object> getBeanMap() {
		return beanMap;
	}

	/**
	 * 用来实现懒加载的方法，根据id查找相应的属性值
	 * @param column 数据库列名
	 * @param fieldName 属性名
	 * @param id 对象的id值
	 * @return 返回封装对象
	 */
	@SuppressWarnings("unchecked")
	public T getProperty(String column,String fieldName, Object id){
		QueryRunner query = new QueryRunner(Configuration.getDataSource());
		String sql = "select " +column+ " as "+ fieldName +" from "+table+" where user.id=?";
		try {
			instance = (T) query.query(sql, new BeanHandler<>(instance.getClass()), id);
			System.out.println(sql);
			return instance;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 对象持久化
	 * @param bean
	 * @throws SQLException
	 */
	public void persist(T bean) throws SQLException {
		QueryRunner query = new QueryRunner(Configuration.getDataSource());
		
		Map<String, Object> map = this.contactInsertSQL(bean);
		String sql = (String) map.get("sql");
		Object[] o = (Object[]) map.get("params");
		int update = query.update(sql,o);
		System.out.println("插入结果" + update);
	}
	

	/**
	 * 更新操作
	 * @param bean
	 * @throws SQLException
	 */
	public void update(T bean) throws SQLException {
		QueryRunner query = new QueryRunner(Configuration.getDataSource());

		Map<String, Object> map = this.contactUpdateSQL(bean);
		String sql = (String) map.get("sql");
		Object[] o = (Object[]) map.get("params");
		int update = query.update(sql,o);
		System.out.println("修改结果" + update);
	}
	
	
	/**
	 * 根据id删除
	 * @param userID
	 * @throws SQLException
	 */
	public void remove(String userID) throws SQLException {
		QueryRunner query = new QueryRunner(Configuration.getDataSource());
		// DELETE FROM 表名称 WHERE 列名称 = 值
		String sql = "DELETE FROM " + table + " WHERE id=?";
		int update = query.update(sql, userID);
		System.out.println("删除结果" + update);
	}
	
	/**
	 * 根据id查找
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public T get(String id) throws SQLException{
		QueryRunner query = new QueryRunner(Configuration.getDataSource());

		String s1 = this.contactSelectSQL();
		String sql = "select " + s1 + " from " + table + " where "+table+".id = ?";
		System.out.println(instance.getClass());
		instance = (T) query.query(sql, new BeanHandler<>(instance.getClass()), id);
		T bean = this.getReturnBean(instance);
		return bean;
	}
	
	/**
	 * 条件查找
	 * @param <E>
	 * @param fieldname
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public T get(String fieldname, Object value) throws SQLException{
		QueryRunner query = new QueryRunner(Configuration.getDataSource());
		
		Map<String, Object> properties = (Map<String, Object>) beanMap.get("properties");
		Map<String, Object> fieldMap = (Map<String, Object>) properties.get(fieldname); // 属性信息
		String column = (String) fieldMap.get("column");
		String type = (String) fieldMap.get("type");
//		String lazy = (String) fieldMap.get("lazy");
		
		String s1 = this.contactSelectSQL();
		String sql = "select "+ s1+ " from " + table + " where " + table+"."+ column +"=?";
		System.out.println(sql);
		if("String".equals(type)){
			instance = (T) query.query(sql, new BeanHandler<>(instance.getClass()), value.toString());
		}else {
			instance = (T) query.query(sql, new BeanHandler<>(instance.getClass()), value);
		}
		return instance;	
	}
	
	/**
	 * 生成完整的修改的sql语句
	 * @param bean 修改后对象
	 * @return 完整sql语句
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> contactUpdateSQL(T bean) {
		Map<String, Object> properties = (Map<String, Object>) beanMap.get("properties");

		String table = (String) beanMap.get("table");
		String idName = (String) beanMap.get("id");
		String s1 = " ";
		int index = 0;
		Object[] o = new Object[properties.size()+1]; // +1是id不再properties的map中
		
		Field[] fields = instance.getClass().getDeclaredFields(); // 获取类的所有属性
		// 列名称 = 新值
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldName = fields[i].getName();
				Map<String, Object> fieldMap = (Map<String, Object>) properties.get(fieldName);
//				String type = (String) fieldMap.get("type");
				String column = (String) fieldMap.get("column");
				try {
					Field field = instance.getClass().getDeclaredField(fieldName);
					field.setAccessible(true); // 是私有的属性可读，默认是false
					
					o[index++] =  field.get(bean);
					s1 += " " + column + "=?,";
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} catch (NullPointerException e) {
				continue;
			}
		} // end for
		
		s1 = s1.substring(0, s1.length()-1) + " WHERE id=?";
		try {
			Field IdField = instance.getClass().getDeclaredField(idName);
			IdField.setAccessible(true); // 是私有的属性可读，默认是false
			o[index++] =  IdField.get(bean); // 最后放入id的值
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} 
		
		// UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
		String sql = "UPDATE " + table + " SET " + s1;
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("params", o);
		return map;
	}
	
	/**
	 * 拼接前半段的 sql.从select 之后到 from之间
	 * @param beanMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String contactSelectSQL(){
		// property={password={column=user_password, lazy=true, type=String}}
		Map<String, Object> properties = (Map<String, Object>) beanMap.get("properties");
		
		String idName = (String) beanMap.get("id");
		String sql = "id as " + idName;
		Field[] fields = User.class.getDeclaredFields();
		
		for (int i = 0; i < fields.length; i++) {
			try{
				Map<String, Object> fieldMap = (Map<String, Object>) properties.get(fields[i].getName());
				String lazy = (String) fieldMap.get("lazy");
				if("false".equals(lazy)){
					String column = (String) fieldMap.get("column");
					sql += ", "+ column + " as " +fields[i].getName();
				}
			}catch(NullPointerException e){
				// 属性没有配置在 映射文件中
				continue;
			}
		}
		return sql;
	}
	
	/**
	 * 组织INSERT 语句 和 参数
	 * @param <E>
	 * @param user 要插入的bean
	 * @return 返回map "sql":sql语句； "params": Object[]参数数组
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> contactInsertSQL(T bean) {
		Map<String, Object> properties = (Map<String, Object>) beanMap.get("properties");

		String table = (String) beanMap.get("table");
		String idName = (String) beanMap.get("id");
		String s1 = " ( id,";
		String s2 = " VALUES ( ?,";
		int index = 0;
		Object[] o = new Object[properties.size()+1];
		
		Field[] fields = instance.getClass().getDeclaredFields(); // 获取类的所有属性
		try {
			Field IdField = instance.getClass().getDeclaredField(idName);
			IdField.setAccessible(true); // 是私有的属性可读，默认是false
			o[index++] =  IdField.get(bean); // 先放入id的值
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} 
		
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldName = fields[i].getName();
				Map<String, Object> fieldMap = (Map<String, Object>) properties.get(fieldName);
//				String type = (String) fieldMap.get("type");
				String column = (String) fieldMap.get("column");
				try {
					Field field = instance.getClass().getDeclaredField(fieldName);
					field.setAccessible(true); // 是私有的属性可读，默认是false
					
					o[index++] =  field.get(bean);
					s1 += " " + column + ",";
					s2 += " ?,";
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} catch (NullPointerException e) {
				continue;
			}
		} // end for
		
		s1 = s1.substring(0, s1.length()-1) + ")";
		s2 = s2.substring(0, s2.length()-1) + ")";
		// INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
		String sql = "INSERT " + "INTO " + table + s1+ s2 ;
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("params", o);
		return map;
	}
	
	/**
	 * 确定返回是对象的代理还是该对象
	 * @param bean 对象
	 * @return 代理或对象
	 */
	@SuppressWarnings("unchecked")
	private T getReturnBean(T bean){
		Map<String, Object> properties = (Map<String, Object>) beanMap.get("properties");
		for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			Map<String, String> property = (Map<String, String>) properties.get(key);
			if("true".equals(property.get("lazy"))){
				BeanProxyHandler handler = new BeanProxyHandler(bean,new Conversation<T>(bean));
				ClassLoader classLoader = instance.getClass().getClassLoader();
				T proxy = (T) Proxy.newProxyInstance(classLoader, bean.getClass().getInterfaces(), handler);
				return proxy;
			}
		}
		return bean;
	}
	
	/**
	 * 判断是否xml 配置了类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean validate(){
		Map<String, Object> userMap = (Map<String, Object>) clazzMap.get(User.class.getName());
		if(userMap.isEmpty()){
			System.out.println("xml 未配置");
			return false;
		}
		return true;
	}

}
