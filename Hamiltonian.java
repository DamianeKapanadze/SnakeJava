class Hamiltonian {
    public static int[][] hamiltonianCycle(int width, int height){
        int[][] x = new int[height+2][width+2];
        int area = height*width;
        height+=2;
        width += 2;
        int i = 2;
        int j = 1;
        for(int k = 0; k < area; k++){
            x[i][j] = k;
            
            if(j % 2 == 1 && i == height-2 && j < width-2) j++;
            else if(j % 2 == 0 && i == 2 && j < width-2) j++;

            else if(i == 2 && j == width-2)  i--;
            else if(i == 1) j--;
            
            else if(j % 2 == 1 && i < height-2) i++;
            else if(j % 2 == 0 && i > 2) i--;
        }
        return x;
    }
    public static void main(String[] args) {
        int[][] x = hamiltonianCycle(20,12);
        for (int i = 0; i < 12+2; i++) {
            for (int j = 0; j < 20+2; j++) {
                System.out.print(x[i][j] + " ");
            }
            System.out.println();
        }
    }
}





/*
class Hamiltonian {
    public static int[][] hamiltonianCycle(int width, int height){
        int[][] x = new int[height][width];
        int i = 1;
        int j = 0;
        for(int k = 0; k < height * width; k++){
            x[i][j] = k;
            
            if(j % 2 == 0 && i == height-1 && j < width-1) j++;
            else if(j % 2 == 1 && i == 1 && j < width-1) j++;

            else if(i == 1 && j == width-1)  i--;
            else if(i == 0) j--;
            
            else if(j % 2 == 0 && i<height-1) i++;
            else if(j % 2 == 1 && i > 1) i--;
        }
        return x;
    }
    public static void main(String[] args) {
        int[][] x = hamiltonianCycle(20,12);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(x[i][j] + " ");
            }
            System.out.println();
        }
    }
}
 */