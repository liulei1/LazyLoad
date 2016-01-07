package cn.ustc.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import cn.ustc.controller.XmlInvaildException;

public class ReadDAOXmlTest {

	public static void main(String[] args) throws DocumentException, XmlInvaildException {
		ReadDAOXmlTest rt = new ReadDAOXmlTest();
		Map<String, Object> dom4jXml = rt.Dom4jXml();
		System.out.println(dom4jXml);
	}

	public Map<String, Object> Dom4jXml() throws DocumentException, XmlInvaildException {
		SAXReader reader = new SAXReader();
		File file = new File(getClass().getClassLoader().getResource("or_mapping.xml").getPath());
		Document document = reader.read(file);
		Element rootNode = document.getRootElement();// action-controller

		Map<String, Object> map = putXmlElementsInMap(rootNode);
		if(map == null){
			throw new XmlInvaildException("解析 controller.xml 文件错误！");
		}
		return map;

	}
	
	/**
	 * 解析XML 文件
	 * @param rootNode XML文件根Element
	 * @return Map<String, Object>
	 * 				result 字符串数组0：type； 1：values
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> putXmlElementsInMap(Element rootNode) {
		Map<String, Object> XmlMap = new HashMap<>();//解析的xml 包含(jdbc,class)
		
		List<Element> elementList = rootNode.elements();
		for (Element element : elementList) {
			
			// 判断是jdbc 还是 class
			if("jdbc".equals(element.getName())){	// 是jdbc
				Map<String, Object> jdbcMap = new HashMap<>();
				
				List<Element> jdbcProperties = element.elements();
				for (Element property : jdbcProperties) {
					String propertyName = property.element("name").getTextTrim();
					String propertyValue = property.element("value").getTextTrim();
					jdbcMap.put(propertyName, propertyValue);
				}
				XmlMap.put("jdbc", jdbcMap);
				
			}else if("class".equals(element.getName())){	// 是<class>
				Map<String, Object> clazzMap = new HashMap<>();
				String clazzName = element.element("name").getTextTrim();
				
				Map<String, Object> clazzPropertyMap = new HashMap<>();
				String clazzTable = element.element("table").getTextTrim();
				String clazzId = element.element("id").element("name").getTextTrim();
				clazzPropertyMap.put("table", clazzTable);
				clazzPropertyMap.put("id", clazzId);
				
				List<Element> propertyElements =element.elements("property"); //<property>
				Map<String, Object> propertyMap = new HashMap<>();
				
				for (Element property : propertyElements) {
					List<Element> tagInProperty = property.elements();  //<property>里面的
					String propertyName=null;
					Map<String, String> propertyValueMap = new HashMap<>();
					for (Element tag : tagInProperty) {
						String tagName = tag.getName();
						String tagText = tag.getTextTrim();
						if("name".equals(tagName)){
							propertyName = tagText;
						}else {
							propertyValueMap.put(tagName, tagText);
						}
					}
					propertyMap.put(propertyName, propertyValueMap);
				}
				clazzPropertyMap.put("property", propertyMap);
				clazzMap.put(clazzName, clazzPropertyMap);
				XmlMap.put("class", clazzMap);
			}
		}
		
		return XmlMap;
	}
}
