package atguigu;

import com.itheima.dao.BookDao;
import com.itheima.dao.impl.BookDaoImpl;

public class Test {

    private BookDao bookDao = new BookDaoImpl();

    @org.junit.Test
    public void testLock() throws Exception  {
        new Thread(new LockRunner("线程1")).start();
        new Thread(new LockRunner("线程2")).start();
        new Thread(new LockRunner("线程3")).start();
        new Thread(new LockRunner("线程4")).start();
        new Thread(new LockRunner("线程5")).start();
        Thread.sleep(200000L);
    }

    class LockRunner implements Runnable {

        private String name;

        public LockRunner(String name) {
            this.name = name;
        }

        public void run() {
            try {
                bookDao.queryBookList(name);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
