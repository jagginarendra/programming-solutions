package com.demo;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MainApp {


    public static void main(String[] args) throws InterruptedException {


     /*   Node leftleft = new Node(5, null, null);
        Node leftright = new Node(15, null, null);
        Node left = new Node(10, leftleft, leftright);
        Node rightleft = new Node(25, null, null);
        Node rightrightright = new Node(38, null, null);
        Node rightright = new Node(35, null, rightrightright);
        Node right = new Node(30, rightleft, rightright);
        Node root = new Node(20, left, right);
        //printRightViewTree(root);
        //printLeftViewTree(root);
        //inOrderTraversal(root);*/


        ReentrantLock reentrantLock = new ReentrantLock();
        Condition oddCondition = reentrantLock.newCondition();
        Condition evenCondition = reentrantLock.newCondition();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new OddWorker(10, reentrantLock, oddCondition, evenCondition));
        executorService.submit(new EvenWorker(10, reentrantLock, oddCondition, evenCondition));
        executorService.shutdown();


    }


    private static void inOrderTraversal(Node current) {

        if (current.left != null)
            inOrderTraversal(current.left);

        System.out.println(current.item);

        if (current.right != null)
            inOrderTraversal(current.right);
    }

    private static void printLeftViewTree(Node root) {
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
        int level = 0;
        processNode(root, map, level);
        System.out.println(map);
    }

    private static void processNode(Node current, LinkedHashMap<Integer, Integer> map, int level) {

        if (current == null) {
            return;
        }

        if (!map.containsKey(level)) {
            map.put(level, current.item);
        }
        //processLeft
        processNode(current.left, map, level + 1);
        //processRight
        processNode(current.right, map, level + 1);

    }

    private static void printRightViewTree(Node root) {
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
        int level = 0;
        processNode2(root, map, level);
        System.out.println(map);
    }

    private static void processNode2(Node current, LinkedHashMap<Integer, Integer> map, int level) {
        if (current == null) {
            return;
        }

        if (!map.containsKey(level)) {
            map.put(level, current.item);
        }

        //processRight
        processNode2(current.right, map, level + 1);

        //processLeft
        processNode2(current.left, map, level + 1);

    }
}


class Node {

    int item;
    public Node right;
    public Node left;

    public Node(int item, Node left, Node right) {
        this.item = item;
        this.right = right;
        this.left = left;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }
}

class CTThreadPoolExecutor extends ThreadPoolExecutor {

    public CTThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        super(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5, true));
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }
}


class MyRunnable implements Runnable {

    @Override
    public void run() {
        //System.out.println("Started "+Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Ended " + Thread.currentThread().getName());

    }
}


class OddWorker implements Runnable {
    private int upto;
    private ReentrantLock reentrantLock;
    private Condition oddCondition;
    private Condition evenCondition;

    public OddWorker(int upto, ReentrantLock reentrantLock, Condition oddCondition, Condition evenCondition) {
        this.upto = upto;
        this.reentrantLock = reentrantLock;
        this.oddCondition = oddCondition;
        this.evenCondition = evenCondition;
    }

    @Override
    public void run() {

        int counter = 1;

        try {
            reentrantLock.lock();
            while (counter < upto) {
                counter = printNumber(counter);
                try {
                    evenCondition.signal();
                    oddCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            //System.out.println("releasing lock");
            reentrantLock.unlock();
        }

    }

    int printNumber(int counter) {
        boolean flag = true;
        while (flag) {
            if (counter % 2 != 0) {
                System.out.print(counter + " ");
                counter++;
                break;
            } else {
                counter++;
            }
        }
        return counter;
    }

}

class EvenWorker implements Runnable {

    private int upto;
    private ReentrantLock reentrantLock;
    private Condition oddCondition;
    private Condition evenCondition;

    public EvenWorker(int upto, ReentrantLock reentrantLock, Condition oddCondition, Condition evenCondition) {
        this.upto = upto;
        this.reentrantLock = reentrantLock;
        this.oddCondition = oddCondition;
        this.evenCondition = evenCondition;
    }

    @Override
    public void run() {

        int counter = 1;

        try {
            reentrantLock.lock();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (counter <= upto) {
                counter = printNumber(counter);
                try {
                    oddCondition.signal();
                    evenCondition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private int printNumber(int counter) {
        boolean flag = true;
        while (flag) {
            if (counter % 2 == 0) {
                System.out.print(counter + " ");
                counter++;
                break;
            } else {
                counter++;
            }
        }
        return counter;
    }


}






