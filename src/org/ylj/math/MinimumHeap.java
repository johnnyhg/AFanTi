package org.ylj.math;

import java.util.Comparator;

public class MinimumHeap<E>{

		Object[] arrary;
		final int   topIndex=1;
		 
		int endIndex=1;
		int tailIndex;
		Comparator<Object> comparator=null;;
		
		public MinimumHeap(int size,Comparator<?> com)
		{
		
			arrary= new Object[size+1];
			endIndex=size;
			tailIndex=1;
			comparator=(Comparator<Object>) com;
			
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
			Object temp=arrary[a_index];
			arrary[a_index]=arrary[b_index];
			arrary[b_index]=temp;
			
		}

		private void adjustFromTop(int rootIndex)
		{
			Object leftChild=null;
			Object rightChild=null;
			Object rootNode=null;
			
			int leftChild_index=rootIndex*2;
			int rightChild_index=rootIndex*2+1;
			
			if(rootIndex<tailIndex)
				rootNode=arrary[rootIndex];
			
			if(leftChild_index<tailIndex)
				leftChild=arrary[leftChild_index];
			
			if(rightChild_index<tailIndex)
				rightChild=arrary[rightChild_index];
			
			if(leftChild==null)
				return ;		
		
			if(rightChild==null)
			{
				if(comparator.compare(rightChild,rootNode)<0)
				{
					swap(leftChild_index,rootIndex);
					return;
				}
			}
			
			Object smallerChild=comparator.compare(leftChild,rightChild)<0?leftChild:rightChild;
			
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
			Object child=arrary[leafIndex];
			Object parent=arrary[parent_index];
			
			if(comparator.compare(child, parent)<0)
			{
				swap(leafIndex,parent_index);	
				adjustFromBottom(parent_index);
			}
			
		}
		private int addAtTail(Object element)
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
			
			Object topElement= arrary[topIndex];
			arrary[topIndex]=arrary[--tailIndex];
			adjustFromTop(topIndex);
			
			return (E)topElement;
			
		}

		public int insertAtTail(Object element)
		{
			int leaf=addAtTail(element);
			adjustFromBottom(leaf);
			return leaf;
		}
		
		
}
