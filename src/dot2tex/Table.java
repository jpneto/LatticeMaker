package dot2tex;

import java.util.HashMap;

public class Table<T> {
    HashMap<Integer, HashMap<Integer, T>> data;
    HashMap<Integer,T> row;
    
    public Table() {
        data = new HashMap<Integer, HashMap<Integer, T>>();
    }
    
    public void put(Integer i, Integer j, T x) {
        if (data.containsKey(i)) {
            row = data.get(i);
        } else {
            row = new HashMap<Integer,T>();
        }
         
        row.put(j,x);
        
        data.put(i,row);
    }
    
    public T get(Integer i, Integer j) {
        if (data.containsKey(i)) {
            row = data.get(i);
        } else {
            return null;
        }
        
        if (row.containsKey(j)) {
            return row.get(j);
        } else {
            return null;
        }
    }
    
    public boolean containsKey(Integer i, Integer j) {
        if (data.containsKey(i)) {
            row = data.get(i);
        } else {
            return false;
        }
        
        return row.containsKey(j);
    }

}