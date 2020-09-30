package www.tq.chatroom.unit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUnit {

    private volatile  static  ThreadUnit threadUnit = null;
    /*创建一个定长的线程池，每提交一个任务就创建一个线程，直到达到池的最大长度，这时线程池会保持长度不再变化*/
    private ExecutorService fixedThreadPool = null;
    /*创建一个可缓存的线程池，如果当前线程池的长度超过了处理的需要时，它可以灵活的回收空闲的线程，当需要增加时，
    它可以灵活的添加新的线程，而不会对池的长度作任何限制*/
    private ExecutorService cachedThreadPool = null;
    /*创建一个定长的线程池，而且支持定时的以及周期性的任务执行，类似于Timer*/
    private ExecutorService scheduledExecutorService = null;
    /*创建一个单线程化的executor，它只创建唯一的worker线程来执行任务*/
    private ExecutorService singleThreadExecutor = null;

    private ThreadUnit(){
        cachedThreadPool = Executors.newCachedThreadPool();
        fixedThreadPool = Executors.newFixedThreadPool(10);
        scheduledExecutorService = Executors.newScheduledThreadPool(10);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static ThreadUnit getInstance(){
        if (threadUnit==null){
            synchronized (ThreadUnit.class)
            {
                if (threadUnit==null){
                    threadUnit = new ThreadUnit();
                }
            }
        }
        return threadUnit;
    }

    public ExecutorService getCachedThreadPool(){
        return cachedThreadPool;
    }

    public ExecutorService getFixedThreadPool(){
        return fixedThreadPool;
    }

    public ExecutorService getScheduledExecutorService(){
        return  scheduledExecutorService;
    }

    public ExecutorService getSingleThreadExecutor(){
        return  singleThreadExecutor;
    }
    public void shutdown(){
        if (fixedThreadPool!=null&&!fixedThreadPool.isShutdown())
            fixedThreadPool.shutdown();
        if (cachedThreadPool!=null&&!cachedThreadPool.isShutdown())
            cachedThreadPool.shutdown();
        if (scheduledExecutorService!=null&&!scheduledExecutorService.isShutdown())
            scheduledExecutorService.shutdown();
        if (singleThreadExecutor!=null&&!singleThreadExecutor.isShutdown())
            singleThreadExecutor.shutdown();
    }
}
