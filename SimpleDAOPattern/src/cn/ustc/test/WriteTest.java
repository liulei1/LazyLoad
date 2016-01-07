package cn.ustc.test;

import java.lang.reflect.Field;

import org.junit.Test;

import cn.ustc.domain.User;

public class WriteTest {
	@Test
	public void test() {
		Field[] fields = User.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			System.out.println(i + " " +fields[i].getName());
		}
	}
}
