
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode start=null;
	private HeapNode min=null;
	private int size=0;
	private static int links=0;
	private  int potential=0;
	private static int cuts=0;

	public HeapNode getStart() {
		return start;
	}

	public void setStart(HeapNode start) {
		this.start = start;
	}

/**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	if (start!=null)
    		return false;
    	return true; // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode=new HeapNode(key);
    	size++;
    	potential++;
    	
    	if(start==null) 
    	{
    		newNode.setNext(newNode);
    		newNode.setPrev(newNode);
    		start=min=newNode;
    	}
    	
    	
    	else
    	{
    		HeapNode last=start.getPrev();
    		newNode.setNext(start);
    		newNode.setPrev(last);
    		start.setPrev(newNode);
    		last.setNext(newNode);
    		if(key<min.getKey()) {
    			this.min=newNode;
    		}
    		this.start=newNode;
    		
    	}
    	return newNode; 
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	if(this.size==1) {// the heap only has one node
     		this.min=null;
     		this.size=0;
     		this.start=null;
     		potential--;
     	}
     	
     	else {
     		this.size--;
     		HeapNode nextMin=this.min.getNext();//next of min
     		HeapNode prevMin=this.min.getPrev();// prev of min
     		if(this.min.getRank()!=0) {//min has kids
     			HeapNode childMin=this.min.getChild();
     			potential--;//delete tree of min
     			noParent(childMin);
     			if(nextMin!=this.min)// min isnt the only tree
     			{
     			prevMin.setNext(childMin);
     			childMin.getPrev().setNext(nextMin);
     			nextMin.setPrev(childMin.getPrev());
     			childMin.setPrev(prevMin);
     			}
     			if(this.start==this.min) {
     				this.start=childMin;
     				
     			}
     			
     		}
     		else {//min doesnt have kids
     			potential--;
     			prevMin.setNext(nextMin);
     			nextMin.setPrev(prevMin);
     			
     			if(this.start==this.min)
     			{
     				this.start=nextMin;
     			}
     		}
     		FibonacciHeap t=Consolidating(this);
     		this.min=t.min;
     		this.start=t.start;
     		
     		
     		
     	}
     	
 	
 		

     	
     	
     	
    }
    // replace the parent to null to all brothers of x
    private void noParent(HeapNode x) {
    	x.setParent(null);
    	if(x.getMark()==true) {
			potential-=2;
		}
    	x.setMark(false);
    	potential++;
    	HeapNode y=x.getNext();
    	while(y!=x) {
    		potential++;
    		y.setParent(null);
    		if(y.getMark()==true) {
    			potential-=2;
    		}
    		y.setMark(false);
    		y=y.getNext();
    		
    	}
    
    	
    }
    //links the trees in f to a binomial heap
    private FibonacciHeap Consolidating(FibonacciHeap f) {
    	HeapNode[] B=toBuckets(f);
    	return fromBuckets(B);
    }
    
    private HeapNode[] toBuckets(FibonacciHeap x) {
    	
    	double logn=(Math.log(size)/Math.log(2));
    	int max=(int) (Math.floor((logn)+1));// the largest rank of tree
    	max=2*max;
    	HeapNode[] B=new HeapNode[max];
    	HeapNode curr=x.start;
    	curr.prev.setNext(null);
    	while(curr!=null) {
    		HeapNode y=curr;
    		curr=curr.next;
    		y.setNext(null);
    		y.setPrev(null);
    		while(B[y.getRank()]!=null) {
    			if (y.getKey()<B[y.getRank()].getKey())
    				y=link(y,B[y.getRank()]);
    			
    			else
    				y=link(B[y.getRank()],y);
    			potential--;
    			B[y.getRank()-1]=null;
    		}
    		B[y.getRank()]=y;
    	}
    	
    	return B;
    }
    
    private FibonacciHeap fromBuckets(HeapNode[] B) {
    	
    	int n=B.length;
    	FibonacciHeap res=new FibonacciHeap();
    	for(int i=B.length-1;i>=0;i--) {
    		if(B[i]!=null) {
    			res.insertTree(B[i]);
    		}
    	}
    	return res;
    	
    }
    // x is a root of a tree
    // inserts x to to current FibonacciHeap
    private void insertTree(HeapNode x) {
    	if(this.start==null) 
    	{
    		x.setNext(x);
    		x.setPrev(x);
    		this.start=min=x;
    		
    	
    	}
    	
    	
    	else
    	{
    		HeapNode last=this.start.getPrev();
    		x.setNext(this.start);
    		x.setPrev(last);
    		this.start.setPrev(x);
    		last.setNext(x);
    		if(x.getKey()<min.getKey()) {
    			this.min=x;
    		}
    		this.start=x;
    		
    	}
    	
    	
    	
    	
    	
    }
    
    
    // links the small tree with the big tree
    // pre: key of small<key of big
    private HeapNode link(HeapNode small,HeapNode big) {
    	
    	
    		HeapNode child1=small.getChild();
    		if(child1!=null) {
    			big.setNext(child1);
    			big.setPrev(child1.getPrev());
    			child1.setPrev(big);
    			big.prev.setNext(big);
    			small.setChild(big);
    			big.setParent(small);
    			small.setRank(small.getRank()+1);
    		}
    		else {// if there is no child-rank=0
    			big.setNext(big);
    			big.setPrev(big);
    			small.setChild(big);
    			big.setParent(small);
    			small.setRank(small.getRank()+1);
    			
    		}
    		//small.setSize(small.getSize()+big.getSize());
    		links++;
    	
    		return small;
    	

    	
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    { 
    	this.potential=this.potential+heap2.potential;
    	HeapNode start1=this.start;
	    HeapNode start2=heap2.start;
	    this.size=this.size+heap2.size;
	  if(!this.isEmpty()&&!heap2.isEmpty()) {
    	  HeapNode last1=this.start.getPrev();
    	  HeapNode last2=heap2.start.getPrev();
    	  last1.setNext(start2);
    	  start2.setPrev(last1);
    	  last2.setNext(start1);
    	  start1.setPrev(last2);
    	  if(heap2.min.getKey()<this.min.getKey()) {
    		  this.min=heap2.min;
    	  }  
	  }
	  else {
		  if(this.isEmpty()) {
			  this.start=heap2.start;
			  this.min=heap2.min;
		  }
		  
	  }
    	  
    	  
    	  
    	  
    	  
    	  
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	double logn=(Math.log(size)/Math.log(2));
    	int max=(int) (Math.floor((logn)+1));// the largest rank of tree
    	max=2*max;
    	int[] res=new int[max];
    	if(!this.isEmpty()) {
    		HeapNode curr=this.start;//start of the list
    		res[curr.getRank()]++;
    		curr=curr.getNext();
    		while(curr!=this.start) {
    			res[curr.getRank()]++;
    			curr=curr.getNext();
    		}
    		
    	}
    	return res;
	
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	decreaseKey(x, x.getKey()-min.getKey()+1);
    	this.min=x;
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    { 
    	x.setKey(x.getKey()-delta);
    	HeapNode parent=x.getParent();
    	if(parent!=null) {
    		if(x.getKey()<parent.getKey()) {
    			cascadingCuts(x,parent);
    		}
    	
    		
    	}
    	else {
    		if(x.getKey()<this.min.getKey()) {
    			this.min=x;
    		}
    	}
    	
    
    	
    	
    	
    }
    
    public void cascadingCuts(HeapNode x,HeapNode parent)
    {
    	cut(x,parent);
    	if(parent!=null) {
    		if (parent.getMark())
    		{

    			cascadingCuts(parent,parent.getParent());
    		}
    		
    		else
    		{
    			if(parent.getParent()!=null) {
    			parent.setMark(true);
    			potential+=2;
    			}
    		}
    	}
    }
    
    public void cut(HeapNode x,HeapNode parent)
    {
    	cuts++;
    	x.setParent(null);
    	if(x.getMark()) {
    		potential-=2;
    	}
    	x.setMark(false);
    	parent.setRank(parent.getRank()-1);
    	if(x.getNext()==x) {
    		parent.child=null;
    	}
    	else {
    		x.getNext().setPrev(x.getPrev());
			x.getPrev().setNext(x.getNext());
    		if(parent.getChild()==x) {
    			parent.setChild(x.getNext());
    		}
    	}
    	insertTree(x);
    	potential++;
    	
    	
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return potential;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k];
        FibonacciHeap temp=new FibonacciHeap();
        HeapNode x,minForArray;
        
       if(H.isEmpty() || k==0)
       {
    	   return arr;
       }
       
       x=temp.insert(H.min.getKey());
       x.setOrigChild(H.min.getChild());
       
       
       for(int i=0;i<k;i++) {
    	   
    	   minForArray=temp.findMin();
    	   arr[i]=minForArray.getKey();
    	   if(minForArray.getOrigChild()!=null) {
    		   insertChild(minForArray.getOrigChild(),temp);
    	   }
    	   temp.deleteMin();
    	   
    	   
       }
       
        
        return arr; 
    }
    private static void insertChild(HeapNode x, FibonacciHeap f) {
    	HeapNode y=x;
    	HeapNode z=f.insert(y.getKey());
    	z.setOrigChild(y.getChild());
    	y=y.getNext();
    	while(y!=x) {
    		z=f.insert(y.getKey());
        	z.setOrigChild(y.getChild());
        	y=y.getNext();
    	}
    	
    }
    
   /**
    * public class HeapNode
    * 
    *  
    */
    public class HeapNode{
    	
    	
    public int key;
    private int rank=0;
    private boolean mark=false;
	private HeapNode child=null;
    private HeapNode next=null;
    private HeapNode prev=null;
    private HeapNode parent=null;
    private HeapNode origChild=null;
    
    
    public HeapNode getOrigChild() {
		return origChild;
	}



	public void setOrigChild(HeapNode origChild) {
		this.origChild = origChild;
	}



	public HeapNode(int key) {
	    this.key = key;
      }


    
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean getMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public HeapNode getChild() {
		return child;
	}

	public void setChild(HeapNode child) {
		this.child = child;
	}

	public HeapNode getNext() {
		return next;
	}

	public void setNext(HeapNode next) {
		this.next = next;
	}

	public HeapNode getPrev() {
		return prev;
	}

	public void setPrev(HeapNode prev) {
		this.prev = prev;
	}

	public HeapNode getParent() {
		return parent;
	}

	public void setParent(HeapNode parent) {
		this.parent = parent;
	}

	
  	public int getKey() {
	    return this.key;
      }
  	
  	public void setKey(int key) {
		this.key = key;
	}


    }
    
    
    
}

