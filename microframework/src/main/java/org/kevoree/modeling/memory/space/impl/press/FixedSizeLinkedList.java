package org.kevoree.modeling.memory.space.impl.press;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* TODO this class is not thread safe */
public class FixedSizeLinkedList implements PressFIFO {

    private int[] _previous;
    private int[] _next;
    private int _head;
    private Random _random;
    private AtomicInteger _magic;

    public FixedSizeLinkedList(int max) {
        this._previous = new int[max];
        this._next = new int[max];
        _head = -1;
        _random = new Random();
        _magic = new AtomicInteger(-1);
    }

    @Override
    public void enqueue(int index) {

        int localMagic;
        do {
            localMagic = _random.nextInt();
        } while (!_magic.compareAndSet(-1, localMagic));

        if (_head == -1) {
            this._next[index] = index;
            this._previous[index] = index;
            _head = index;
        } else {
            int currentHead = this._head;
            int currentPrevious = this._previous[_head];
            _head = index;
            //chain previous
            this._previous[index] = currentPrevious;
            this._next[currentPrevious] = index;

            this._previous[currentHead] = index;
            this._next[index] = currentHead;
        }

        _magic.compareAndSet(localMagic, -1);

    }

    @Override
    public int dequeue() {

        int localMagic;
        do {
            localMagic = _random.nextInt();
        } while (!_magic.compareAndSet(-1, localMagic));

        int currentHead = _head;
        if (currentHead != -1) {
            //circular ring, take previous
            int tail = this._previous[currentHead];
            int previous = this._previous[tail];
            this._next[previous] = _head;
            this._previous[_head] = previous;
            _magic.compareAndSet(localMagic, -1);
            return tail;
        } else {
            _magic.compareAndSet(localMagic, -1);
            return -1;
        }
    }

}