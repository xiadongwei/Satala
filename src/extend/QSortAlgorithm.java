package extend;

public class QSortAlgorithm 
{
    
   void QuickSort(Integer a[], int lo0, int hi0) throws Exception
   {
      int lo = lo0;
      int hi = hi0;
      int mid;

      if ( hi0 > lo0)
      {

    	 mid = a[ ( lo0 + hi0 ) / 2 ].intValue();

         while( lo <= hi )
         {

         while( ( lo < hi0 ) && ( a[lo].intValue() < mid ))
        	 ++lo;

           
	     while( ( hi > lo0 ) && ( a[hi].intValue() > mid ))
		 --hi;
            
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         
         if( lo0 < hi )
            QuickSort( a, lo0, hi );

         
         if( lo < hi0 )
            QuickSort( a, lo, hi0 );

      }
   }

   private void swap(Integer a[], int i, int j)
   {
      Integer T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;

   }

   public void sort(Integer a[]) throws Exception
   {
	   QuickSort(a, 0, a.length - 1);
   }
}
