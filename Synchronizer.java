import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Synchronizer<T, V> {
	private final Set<V> groups = new HashSet<V>();
	private final Map<T, V> elementToGroupMapping = new HashMap<T, V>();
	private final Map<V, Lock> groupToLockMapping = new HashMap<V, Lock>();
	private final Map<Lock, Condition> lockToConditionMapping = new HashMap<Lock, Condition>();
	
	public void add(T element, V group) {
		if (!groups.contains(group)) {
			groups.add(group);
			Lock groupLock = new ReentrantLock();
			Condition lockCondition = groupLock.newCondition();
			groupToLockMapping.put(group, groupLock);
			lockToConditionMapping.put(groupLock, lockCondition);
		}
		elementToGroupMapping.put(element, group);
	}
	
	public void remove(T element) {
		elementToGroupMapping.remove(element);
	}
	
	public V getElementGroup(T element) {
		return elementToGroupMapping.get(element);
	}
	
	private Lock getGroupLock(V group) {
		return groupToLockMapping.get(group);
	}
	
	public Lock getElementLock(T element) {
		return getGroupLock(getElementGroup(element));
	}
	
	private Condition getLockCondition(Lock lock) {
		return lockToConditionMapping.get(lock);
	}
	
	public Condition getElementCondition(T element) {
		return getLockCondition(getGroupLock(getElementGroup(element)));
	}
}
