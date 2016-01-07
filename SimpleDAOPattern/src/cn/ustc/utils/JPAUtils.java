package cn.ustc.utils;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cn.ustc.domain.Configuration;

public class JPAUtils {
	/**
	 * 获取实体管理类 实例
	 * @param persistenceUnitName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static EntityManager getEntityManager(String persistenceUnitName){
//		Map<String, String> jdbcMap = (Map<String, String>) Configuration.xmlMap.get("jdbc");
//		Map<String, String> properties = new HashMap<>();
//		properties.put("javax.persistence.jdbc.driver", jdbcMap.get("driverClass"));
//		properties.put("javax.persistence.jdbc.url", jdbcMap.get("jdbcUrl"));
//		properties.put("javax.persistence.jdbc.user", jdbcMap.get("user"));
//		properties.put("javax.persistence.jdbc.password", jdbcMap.get("password"));
		
		EntityManagerFactory emf  =  Persistence.createEntityManagerFactory("user");
		EntityManager em = emf.createEntityManager();
		return em;
	}
}
