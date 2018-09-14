package objectsize;


import com.javamex.classmexer.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行main方法需在JVM启动参数中添加-javaagent:/path_to_agent/classmexer.jarobj
 */
public class TestObjectSize {


	public static void main(String[] args) {

		ObjectBaseType ob = new ObjectBaseType();
		ObjectBaseObject obo = new ObjectBaseObject();
		ObjectBaseArr oba = new ObjectBaseArr();
		char[] c = new char[4];
		ObjectString os = new ObjectString();
		List l = new ArrayList();
		List bl = new ArrayList(100);
		List sl = new ArrayList(100);
		for (int i = 0; i < 100; i++) {
//			sl.add(String.valueOf(System.nanoTime()));
//			sl.add("a");
			sl.add(new String("a"));
		}

		System.out.println("ObjectBaseType sd:"+MemoryUtil.memoryUsageOf(ob));
		System.out.println("ObjectBaseType rs:"+MemoryUtil.deepMemoryUsageOf(ob));
		System.out.println("ObjectBaseObject sd:"+MemoryUtil.memoryUsageOf(obo));
		System.out.println("ObjectBaseObject rs:"+MemoryUtil.deepMemoryUsageOf(obo));
		System.out.println("c sd:"+MemoryUtil.memoryUsageOf(c));
		System.out.println("c rs:"+MemoryUtil.deepMemoryUsageOf(c));
		System.out.println("ObjectBaseArr sd:"+MemoryUtil.memoryUsageOf(oba));
		System.out.println("ObjectBaseArr rs:"+MemoryUtil.deepMemoryUsageOf(oba));
		System.out.println("ObjectString sd:"+MemoryUtil.memoryUsageOf(os));
		System.out.println("ObjectString rs:"+MemoryUtil.deepMemoryUsageOf(os));
		String e = new String("a");
		System.out.println("String sd:"+MemoryUtil.memoryUsageOf(e));
		System.out.println("String rs:"+MemoryUtil.deepMemoryUsageOf(e));
		System.out.println("ArrayList() sd:"+MemoryUtil.memoryUsageOf(l));
		System.out.println("ArrayList() rs:"+MemoryUtil.deepMemoryUsageOf(l));
		System.out.println("ArrayList(100) sd:"+MemoryUtil.memoryUsageOf(bl));
		System.out.println("ArrayList(100) rs:"+MemoryUtil.deepMemoryUsageOf(bl));
		System.out.println("ArrayList(100) String sd:"+MemoryUtil.memoryUsageOf(sl));
		System.out.println("ArrayList(100) String rs:"+MemoryUtil.deepMemoryUsageOf(sl));


		e = String.valueOf(System.nanoTime());
		System.out.println(e.length());
		System.out.println("String sd:"+MemoryUtil.memoryUsageOf(e));
		System.out.println("String rs:"+MemoryUtil.deepMemoryUsageOf(e));
		e = "";
		System.out.println("String sd:"+MemoryUtil.memoryUsageOf(e));
		System.out.println("String rs:"+MemoryUtil.deepMemoryUsageOf(e));
	}

}
