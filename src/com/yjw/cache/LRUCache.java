package com.yjw.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K,V> {
	private final int MAX_CHCHE_SIZE;
	Map<K,Entry<K,V>> cache = null;
	//在链表中尽量推荐头节点，first节点做复杂操作很麻烦
	Entry<K, V> head;
	Entry<K, V> last;
	
	public LRUCache(int size) {
		MAX_CHCHE_SIZE = size;
		head = new Entry<>();
		cache = new HashMap<>();
	}
	
	public void put(K key,V value) {
		if(cache.containsKey(key)) {
			System.out.println("new cache override the elder one for : "+key);
		}
		//如果满了，remove掉最早的那一个缓冲
		if(cache.size() >= MAX_CHCHE_SIZE)
			removeLastEntry();
		
		Entry<K, V> entry = getEntry(key);
		//如果cache中没有这个key，返回null
		if(entry == null) {
			entry = new Entry<K,V>();
			entry.key = key;
		}
		entry.value = value;
		//现在是刚插进来，根据LRU准则，将其移至链表头
		moveEntryToFirst(entry);
		cache.put(key,entry);
	}
	
	public V get(K key) {
		//每次新get也视为最近使用了，放到队列之前
		Entry<K, V> entry = getEntry(key);
		if(entry != null) {
			moveEntryToFirst(entry);
			return entry.value;
		}
		return null;
	}
	public boolean remove(K key){
		if(!cache.containsKey(key)) return false;
		Entry<K, V> entry = getEntry(key);
		if(entry.pre != null) entry.pre.next = entry.next;
		if(entry.next != null) entry.next.pre = entry.pre;
		if(entry == last) last=last.pre;
		cache.remove(key);
		return true;
	}
	
	private void removeLastEntry(){
		
		if(last == null){
			return;
		}
		//将last从map中删除
		cache.remove(last.key);
		//将last从链表中删除
		last = last.pre;
		last.next = null;
		if(last == head) last = null;
	}
	
	private void moveEntryToFirst(Entry<K, V> entry){
		if(head == null){
			System.out.println("head is null !!!!");
			return;
		}
		//在原链表结构中删除这个节点
		if(entry.pre != null) entry.pre.next = entry.next;
		if(entry.next != null) entry.next.pre = entry.pre;
		
		//如果现在移动的是最后一个
		if(entry == last) last = last.pre;
		
		//把这个节点放到第一个去
		if(head.next == null) {
			head.next = entry;
			entry.pre = head;
			last = entry;
		} else {
			Entry<K, V> tmp = head.next;
			head.next = entry;
			entry.pre = head;
			entry.next = tmp;
			tmp.pre = entry;
		}
	}
	
	private Entry<K,V> getEntry(K key){
		return cache.get(key);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Entry entry  = head.next;
		while(entry != null){
			sb.append("key : "+entry.key+"       value : "+entry.value+"\n");
			entry = entry.next;
		}
		return sb.toString();
	}
	
	
	//设置缓存map中存的值为一个双向链表节点，将链表按照LRU的顺序进行调整
	class Entry<K,V>{
		Entry<K,V> pre;
		Entry<K,V> next;
		K key;
		V value;
	}
	
	public static void main(String[] args) {
		//测试方法
		LRUCache<Integer, Integer> cache = new LRUCache<>(3);
		cache.put(1, 1);
		cache.put(2, 2);
		cache.put(3, 3);
		System.out.println(cache);
		cache.get(1);
		cache.put(4, 4);
		System.out.println();
		System.out.println(cache);
	}
}

