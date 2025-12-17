package com.example.dsaproject.DataStructures;

import com.example.dsaproject.Model.WaitingListEntry;
import java.util.*;

public class BookingPriorityQueue {

    private PriorityQueue<WaitingListEntry> queue;
    private Map<Integer, WaitingListEntry> lookupMap;

    public BookingPriorityQueue() {
        this.queue = new PriorityQueue<>(
                Comparator.comparing(WaitingListEntry::getTimestamp)
        );
        this.lookupMap = new HashMap<>();
    }

    public void add(WaitingListEntry entry) {
        queue.offer(entry);
        lookupMap.put(entry.getBookingId(), entry);
    }

    public WaitingListEntry pollNext() {
        WaitingListEntry entry = queue.poll();
        if (entry != null) {
            lookupMap.remove(entry.getBookingId());
        }
        return entry;
    }

    public WaitingListEntry peekNext() {
        return queue.peek();
    }

    public boolean contains(int bookingId) {
        return lookupMap.containsKey(bookingId);
    }

    public boolean remove(int bookingId) {
        WaitingListEntry entry = lookupMap.get(bookingId);
        if (entry != null) {
            queue.remove(entry);
            lookupMap.remove(bookingId);
            return true;
        }
        return false;
    }

    public List<WaitingListEntry> getAllSorted() {
        List<WaitingListEntry> sorted = new ArrayList<>(queue);
        Collections.sort(sorted, Comparator.comparing(WaitingListEntry::getTimestamp));
        return sorted;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
        lookupMap.clear();
    }
}