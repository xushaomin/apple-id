package com.appleframework.id.codis;

import org.junit.Test;

import com.appleframework.id.CodisIdGenerator;
import com.appleframework.id.IdentityGenerator;

import junit.framework.TestCase;

public class TestCodisIdGenerator extends TestCase {

	@Test
	public void test1() throws Exception {

		String zkAddr = "192.168.1.182:2181,192.168.1.183:2181,192.168.1.184:2181";
		String zkProxyDir = "/zk/codis/db_jiuzhi/proxy";
		String namespace = "order";

		try {
			IdentityGenerator idGenerator = CodisIdGenerator.getInstance(zkAddr, zkProxyDir);
			idGenerator.setValue(namespace, 1011);
			for (int i = 0; i < 100; i++) {
				System.out.println(idGenerator.nextId(namespace));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
