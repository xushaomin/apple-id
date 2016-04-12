package test.groboutils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.appleframework.id.SnowflakeIdGenerator;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

public class GroboutilsSnowflake {

	/**
	 * 多线程测试用例
	 * 
	 * @author lihzh(One Coder)
	 * @date 2012-6-12 下午9:18:11
	 * @blog http://www.coderli.com
	 */
	@Test
	public void MultiRequestsTest() {
		
		final Map<Long, Long> idMaps = new ConcurrentHashMap<Long, Long>();
		final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();
		
		// 构造一个Runner
		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				// 测试内容
				for (int i = 0; i < 1000; i++) {
					Long id = idGenerator.generateIdMini();
					System.out.println("id: " + id);
	                if (idMaps.containsKey(id)) {
	                    System.out.println("Error: " + id);
	                }
	                idMaps.put(id, id);
				}
			}
		};
		int runnerCount = 1000;
		// Rnner数组，想当于并发多少个。
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			// 开发并发执行数组里定义的内容
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
