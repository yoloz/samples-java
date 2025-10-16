package indi.yolo.sample;

import org.mapdb.DB;
import org.mapdb.IndexTreeList;

/**
 * @author yolo
 */
public class WriteThread implements Runnable {

    private int index;
    private final DB db;

    public WriteThread(DB db) {
        this.db = db;
    }

    public void updateIndex(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        // 库名:test
        IndexTreeList<TestBean> indexTreeList = db.indexTreeList("test", new TestSerializer<>()).createOrOpen();

        TestBean test = new TestBean("testA" + index, "testB" + index, "testC" + index);
        indexTreeList.add(test);

        db.commit();
    }

    public void remove(int index) {
        // 库名:test
        IndexTreeList<TestBean> indexTreeList = db.indexTreeList("test", new TestSerializer<>()).createOrOpen();
        indexTreeList.remove(index);

        db.commit();
    }

    // update 直接属性更新无效，采取先删除再添加
    public void update(TestBean src, TestBean target) {
        // 库名:test
        IndexTreeList<TestBean> indexTreeList = db.indexTreeList("test", new TestSerializer<>()).createOrOpen();
        if (indexTreeList.remove(src)) {
            System.out.println("update " + src + "success");
        }
        indexTreeList.add(target);
        db.commit();
//        indexTreeList.stream().filter(e -> e.getA().equals(src.getA()) && e.getB().equals(src.getB()) && e.getC().equals(src.getC())).findFirst()
//                .ifPresent(
//                        e -> {
//                            e.setA(target.getA());
//                            e.setB(target.getB());
//                            e.setC(target.getC());
//                            System.out.println("update " + src + " to " + target);
//                            db.commit();
//                        }
//                );
    }

    public void remove(TestBean test) {
        // 库名:test
        IndexTreeList<TestBean> indexTreeList = db.indexTreeList("test", new TestSerializer<>()).createOrOpen();
        if (indexTreeList.remove(test)) {
            System.out.println("remove " + test + " success");
            db.commit();
        }
    }
}
