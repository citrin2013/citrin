int main()
{
   int arr[] = {3,2,4,1,0};

   int temp;
   bool isSorted = 0;
   int i;
   while(!isSorted){
      isSorted = 1;
      i = -1;
      while(++i<5-1){
         if(arr[i]>arr[i+1]){
            temp = arr[i];
            arr[i] = arr[i+1];
            arr[i+1] = temp;
            isSorted = 0;
         }
      }
   }
}
