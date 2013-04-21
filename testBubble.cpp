int main()
{
   int arr[5];
   arr[0] = 3;
   arr[1] = 2;
   arr[2] = 4;
   arr[3] = 1;
   arr[4] = 0;

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
