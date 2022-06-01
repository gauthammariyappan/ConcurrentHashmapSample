package map;
import java.util.*;


public class concurmap
{
    // the initial capacity of the bucket is 16 and the fillcount is 0
    public static int capacity = 16,fillcount = 0;

    //node inner class to store key-value pair and next node 
    public static class node<K,V>
    {
        K key;
        volatile V value;
        volatile node<K,V> next;

        public node() {}
        public node(K key,V value)
        {
            this.key = key;
            this.value = value;
            this.next = null;

        }
        public K getKey()
        {
            return key;
        }
        public V getValue()
        {
            return value;
        }
        public void setValue(V newvalue)
        {
            this.value = newvalue;
        }
        
    }

    //to calculate the index
    public static int hashidx(Object obj)
    {
        if( obj == null )
            return 0;
        return ( obj.hashCode() & (capacity-1) );
    }

    //bucket to store all the entries
    public static node[] bucket = new node[capacity]; 

    //to resize the bucket based on fillcount and capacity
    public static void checkCapacity()
    {
       if( fillcount == capacity )
       {
            capacity += capacity;
            node[] temp = new node[capacity];
            for( node k : bucket )
            {
                int n = hashidx(k.key);
                temp[n] = k;
            }
            bucket = new node[capacity];
            bucket = temp.clone();
       }
    }

    //put method to enter entries on to the bucket
    public <K,V> Object put(K key,V value)
    {   
        synchronized (this)//for concurrent access of the threads
        {
            System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" got lock at = "+System.nanoTime());
            try
            {
                // to hold the thread for some time
                System.out.println("Thread Holding......");
                Thread.sleep(2000);
            }
            catch(Exception e){}

            checkCapacity();

            int n = hashidx(key);

            if( bucket[n] == null )//if the index is empty
            {
                bucket[n] = new node(key,value);
                fillcount++;
                System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                return null;
            }
            else//if the index is not empty
            {
                node temp = bucket[n];
                while( temp.next != null )
                {
                    if( key==null && temp.key == key )
                    {
                        Object oldval = temp.value;
                        temp.setValue(value);
                        System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                        return oldval;  
                    }
                    else if( key!=null &&  Objects.equals(key, temp.key) )
                    {
                        Object oldval = temp.value;
                        temp.setValue(value);
                        System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                        return oldval;
                    }
                    temp = temp.next;
                }
                if( key==null && temp.key == key )
                {
                    Object oldval = temp.value;
                    temp.setValue(value);;
                    System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                    return oldval;  
                }
                else if( key!=null && Objects.equals(key, temp.key) )
                {
                    Object oldval = temp.value;
                    temp.setValue(value);
                    System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                    return oldval;
                }
                else
                {
                    temp.next = new node(key,value);
                    temp=temp.next;
                    System.out.println("Thread PUT "+"{ key : "+key+", value : "+value+" }"+" released lock at = "+System.nanoTime());
                    return null;
                }
            }


        }
    }

    //get method to get the entries from the bucket
    public  <K,V> Object get(K key)
    {
        System.out.println("Thread GET "+"{ key : "+key+" }"+" got lock at = "+System.nanoTime());
        int n = hashidx(key);
        if( bucket[n] == null )//if the index is empty
        {
            System.out.println("{ key : "+key+", value : "+null+" }");
            System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
            return null;
        }
        else//if the index is not empty
        {
            node temp = bucket[n];
            while( temp.next != null )
            {
                if( key == null && temp.key == key )
                {
                    System.out.println("{ key : "+key+", value : "+temp.value+" }");
                    System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return temp.value;
                }
                else if( key!=null && Objects.equals(key, temp.key) )
                {
                    System.out.println("{ key : "+key+", value : "+temp.value+" }");
                    System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return temp.value;
                }
                temp = temp.next;
            }
            if( key==null && temp.key == key )
            {
                System.out.println("{ key : "+key+", value : "+temp.value+" }");
                System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                return temp.value;
            }
            else if( key!=null && Objects.equals(key, temp.key) )
            {
                System.out.println("{ key : "+key+", value : "+temp.value+" }");
                System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                return temp.value;
            }
            else
            {
                System.out.println("{ key : "+key+", value : "+null+" }");
                System.out.println("Thread GET "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                return null;
            }

        }
    }

    //to remove an entry from the bucket
    public <K,V> Object remove(K key)
    {
        synchronized (this)// for cuncurrent access of the threads
        {
            System.out.println("Thread remove "+"{ key : "+key+" }"+" got lock at = "+System.nanoTime());

            int n = hashidx(key);
            if( bucket[n] == null )// if the index is empty
            {
                System.out.println("Removed { key : "+key+" , value : "+null+" }");
                System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                return null;
            }
            else// if the index is not empty
            {
                
                node temp = bucket[n];
                if( key == null && temp.key == key )
                {
                    Object oldval=temp.value;
                    bucket[n] = temp.next;
                    if( bucket[n] == null )
                        fillcount--;
                    System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return oldval;  
                }               
                else if( key!=null && Objects.equals(key, temp.key) )
                {
                    Object oldval = temp.value;
                    bucket[n] = temp.next;
                    if( bucket[n] == null )
                        fillcount--;
                    System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return oldval;  
                } 

                node prev = temp;
                temp = temp.next; 

                if(temp==null)
                {
                    System.out.println("Removed { key : "+key+" , value : "+null+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return null;
                }

                while( temp.next != null )
                {
                    if( key == null && temp.key == key )
                    {
                        Object oldval = temp.value;
                        prev.next = temp.next;
                        System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                        System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                        return oldval;  
                    }
                    else if( key!=null && Objects.equals(key, temp.key) )
                    {
                        Object oldval = temp.value;
                        prev.next = temp.next;
                        System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                        System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                        return oldval;  
                    }
                    prev = temp;
                    temp = temp.next;
                }
                if( key == null && temp.key == key )
                {
                    Object oldval = temp.value;
                    prev.next = temp.next;
                    System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return oldval;  
                }               
                else if( key!=null && Objects.equals(key, temp.key) )
                {
                    Object oldval = temp.value;
                    prev.next = temp.next;
                    System.out.println("Removed { key : "+key+" , value : "+oldval+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return oldval;
                }               
                else
                {
                    System.out.println("Removed { key : "+key+" , value : "+null+" }");
                    System.out.println("Thread remove "+"{ key : "+key+" }"+" released lock at = "+System.nanoTime());
                    return null;
                }

            }

            
        }
    }

    // display all the entries on the bucket
    public static void display()
    {
        try
        {        
            Thread.sleep(5000);
        }
        catch(Exception e){}
        
        for(node temp:bucket)
        {
            if( temp!=null )
            {
                System.out.println("--------------------------------");
                System.out.println(hashidx(temp.key));
                node t=temp;
                while( t.next!=null )
                {
                    System.out.print(" ( "+t.getKey()+" , "+t.getValue()+" ) ");
                    t=t.next;
                }
                System.out.println(" ( "+t.getKey()+" , "+t.getValue()+" ) ");
                System.out.println("--------------------------------");
            }
        }
    }

}