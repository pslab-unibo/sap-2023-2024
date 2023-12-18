package acme;

public class Holder<T> {

	private T obj;
	
	public Holder() {
	}
		
	public void set(T obj) {
		this.obj = obj;
	}
	
	public T get() {
		return obj;
	}
}
