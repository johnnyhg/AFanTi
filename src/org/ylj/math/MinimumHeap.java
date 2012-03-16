package org.ylj.math;

import java.util.Comparator;

public class MinimumHeap<E>{

		Object[] arrary;
		final int   topIndex=1;
		 
		int endIndex=1;
		int tailIndex;
		Comparator<E> comparator=null;;
		
		public MinimumHeap(int size,Comparator<E> com)
		{
		
			arrary=  new Object[size+1];
			endIndex=size;
			tailIndex=1;
			comparator=(Comparator<E>) com;
			
		}
		
		/**
		 * 
		 * @param element
		 * @return 0 not find
		 */
		public E getTop()
		{
			if(topIndex==tailIndex)
				return null;
			return (E)arrary[topIndex];
		}
		
	
	
		private void swap(int a_index,int b_index)
		{
			E temp=(E)arrary[a_index];
			arrary[a_index]=arrary[b_index];
			arrary[b_index]=temp;
			
		}

		private void adjustFromTop(int rootIndex)
		{
			E leftChild=null;
			E rightChild=null;
			E rootNode=null;
			
			int leftChild_index=rootIndex*2;
			int rightChild_index=rootIndex*2+1;
			
			if(rootIndex<tailIndex)
				rootNode=(E)arrary[rootIndex];
			
			if(leftChild_index<tailIndex)
				leftChild=(E)arrary[leftChild_index];
			
			if(rightChild_index<tailIndex)
				rightChild=(E)arrary[rightChild_index];
			
			if(leftChild==null)
				return ;		
		
			if(rightChild==null)
			{
				if(comparator.compare(leftChild,rootNode)<0)
				{
					swap(leftChild_index,rootIndex);
					
				}
				return;
			}
			
			
			E smallerChild=comparator.compare(leftChild,rightChild)<0?leftChild:rightChild;
			
			if(comparator.compare(smallerChild,rootNode)<0)
			{
				int smallerChild_index=(smallerChild==rightChild?rightChild_index:leftChild_index);
				
				swap(smallerChild_index,rootIndex);			
				adjustFromTop(smallerChild_index);
				
			}
			
	
			
		}
		
		private void adjustFromBottom(int leafIndex)
		{
			
			int parent_index=leafIndex/2;
			if(parent_index==0)
				return;
			E child=(E)arrary[leafIndex];
			E parent=(E)arrary[parent_index];
			
			if(comparator.compare(child, parent)<0)
			{
				swap(leafIndex,parent_index);	
				adjustFromBottom(parent_index);
			}
			
		}
		private int addAtTail(E element)
		{
			//E obj=(E)element;
			
			if(tailIndex==endIndex+1)
				return 0;
			arrary[tailIndex++]=element;
			return tailIndex-1;
			
		}
		
		
		public boolean isFull()
		{
			if(tailIndex==endIndex+1)
				return true;
			else
				return false;
		}
		public E removeTop()
		{
			
			if(tailIndex==topIndex)
				return null;
			
			E topElement= (E)arrary[topIndex];
			arrary[topIndex]=(E)arrary[--tailIndex];
			adjustFromTop(topIndex);
			
			return topElement;
			
		}

		public int insertAtTail(E element)
		{
			int leaf=addAtTail(element);
			adjustFromBottom(leaf);
			return leaf;
		}
		
		public Object[] toArrary()
		{
			return arrary;
		}
		public E[] toArrary(E[] Earrary)
		{
			for(int i=0;i<Earrary.length;i++)
				Earrary[i]=(E)arrary[i+1];
			return Earrary;
		}
		
		
		
	
		
		
}
