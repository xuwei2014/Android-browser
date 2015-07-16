package com.lingmo.net;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * �������������̳߳�:���Ʋ��е����������̡߳�
 * 
 * </BR> </BR> By����ɬ </BR> ��ϵ���ߣ�QQ 534429149
 * */

public class ThreadPoolUtils {

	private ThreadPoolUtils() {
	}

	// ��������߳����������߳���
	private static int CORE_POOL_SIZE = 3;

	// �̳߳�����߳����������������е��̶߳��Ᵽ����ٸ��߳�
	private static int MAX_POOL_SIZE = 200;

	// �����߳̿���״̬����ʱ��
	private static int KEEP_ALIVE_TIME = 5000;

	// �������С��������̶߳������˷����
	// ��ʼ��һ����СΪ10�ķ���ΪRunnable�Ķ���
	private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
			10);
	// �̹߳���,�Ѵ��ݽ�����runnable��������һ��Thread
	private static ThreadFactory threadFactory = new ThreadFactory() {

		// ԭ���͵�integer�������ɵ�integerֵ�����ظ�
		private final AtomicInteger ineger = new AtomicInteger();

		@Override
		public Thread newThread(Runnable arg0) {
			return new Thread(arg0, "MyThreadPool thread:"
					+ ineger.getAndIncrement());
		}
	};

	// ���̳߳ط����쳣��ʱ��ص�����
	private static RejectedExecutionHandler handler = new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			// ������������
		}

	};
	// �̳߳�ThreadPoolExecutor java�Դ����̳߳�
	private static ThreadPoolExecutor threadpool;
	// ��̬����飬���౻���ص�ʱ�����
	static {
		threadpool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory,
				handler);
	}

	public static void execute(Runnable runnable) {
		threadpool.execute(runnable);
	}
}
