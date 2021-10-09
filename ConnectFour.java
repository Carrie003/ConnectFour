/* 
   Runtime: O(1) for each move for board with any size, since for the worst case, we only check 7 in a row, 7 in diagonal, 7 in antidiagonal, and 3 in the vertical direction.
   Space: O(1) for 6*7 board, and O(m*n) for m*n board.
*/

/*
   Variables: 
   ColumNum : the number of columns of the board (adaptable if we want to change to some other number :))
   RowNum : the number of rows of the board
   Colors : the color of coins for the game, adaptable if we want to change color or switch player's coin colors
   humanScore : stores the maximum streak the human player achieve for the current round
   AIScore : stores the maximum streak the AI player achieve for the current round
   move: the number of moves AI/human player do in total for the current round
   nextRow : stores the next row that the player will place for a given column. It will decrement everytime a player places on the column
   board : stores both players' moves for the current round by changing a cell to 'R' or 'Y' depending the player's coin color. For this implementation, I assumed red to be the human player, and yellow to be the AI player
   lastColor : the color of the last coin placed on the board, used to check if the next color is different from this one. Valid move if it is, invalid otherwise. 
   rand : used to generate a random column for AI player
*/


import java.util.*;
public class ConnectFour {
    private final int ColumnNum = 7;
    private final int RowNum = 6;
    private final char[] Colors = new char[]{'R', 'Y'};
    private int humanScore;
    private int AIScore;
    private int move;
    private int[] nextRow;
    private static char[][] board;
    private char lastColor;
    private Random rand;
    
    public static void main(String[] args) {  
    }
    
    /*
      Constructor of the class. Initializes variables by calling restart method
    */
    public ConnectFour(){
        restart();
    }
    
    /*
      Restarts the game and set all the variables to their initial values
    */
    private void restart(){
        humanScore = 0;
        AIScore = 0;
        move = 0;
        nextRow = new int[ColumnNum];
        board = new char[RowNum][ColumnNum];
        lastColor = '*'; 
        rand = new Random();
        Arrays.fill(nextRow, RowNum-1);
    }
    
    /*
      Method that handles the next move of a player. Checks if it's a valid move. 
      If it is a valid move, check if the player wins and updates the player's score accordingly. 
      Right after the person plays, AI places one move
    */
    private void play(char color, int col){
        if(col < 0 ||  nextRow[col] < 0 || col >= ColumnNum || nextRow[col] >= RowNum || color != Colors[0] && color != Colors[1] || color == lastColor){
            System.out.println("Not a valid move! Try again :)");
            return;
        }
        int row = nextRow[col];
        board[row][col] = color;
        nextRow[col]--;
        lastColor = color;
        move++;
        String player = color == Colors[0] ? "you" : "AI";
        System.out.println("For move " + move + " " + player + " placed a coin on row " + row + " and column " + col);
        int verticalStreak = getVerticalStreak(col, row, color);
        int horizontalStreak = getHorizontalStreak(col, row, color);
        int diagonalStreak = getDiagonalStreak(col, row, color);
        int antidiagonalStreak = getAntidiagonalStreak(col, row, color);
        if(verticalStreak >= 4 || horizontalStreak >= 4 || diagonalStreak >= 4 || antidiagonalStreak >= 4){
            printWinningStatement(color);
        }
        else{
            int currentScore = Math.max(verticalStreak, Math.max(horizontalStreak, Math.max(diagonalStreak, antidiagonalStreak)));
            printScore(color, currentScore);
            if(move == ColumnNum * RowNum){
                System.out.println("It's a tie!");
                restart();
            } 
            else if(color == Colors[0]){
                AIPlay();
            }
        }
    }

    /*
      Method that handles the next move of the AI player. The column number of AI player's move is randomly generated
    */
    private void AIPlay(){
        int randomCol = rand.nextInt(ColumnNum);
        play(Colors[1], randomCol);
    }
    
    /*
      Gets the maximum vertical streak after the current move. It counts a maximum of 7 cells, 
      starting from 3 cells to the left of the cell of current move, 
      to the 3 cells to the right of the cell of current move. 
      If the cells are out of bound, then starts/ends from the boundaries of the board.
    */
    private int getVerticalStreak(int col, int row, char color){
        int currentStreak = 0;
        int maxStreak = 1;
        for(int newRow = row; newRow < Math.min(row + 4, RowNum); newRow++){
            if(color == board[newRow][col]){
                currentStreak++;
                maxStreak = Math.max(currentStreak, maxStreak);
            }
            else{
                currentStreak = 0;
            }
        }
        return maxStreak;
    }
    
    /*
      Gets the maximum horizontal streak after the current move. 
      It counts a maximum of 3 cells, starting from the cell under the current move 
      to the bottom of the row 
      (since the coins fall with gravity to the lowest unoccupied row, 
      we don't have cells filled above the cell of current move).
    */
    private int getHorizontalStreak(int col, int row, char color){
        int currentStreak = 0;
        int maxStreak = 1;
        for(int newCol = Math.max(col - 3, 0); newCol < Math.min(col + 4, ColumnNum); newCol++){
            if(color == board[row][newCol]){
                currentStreak++;
                maxStreak = Math.max(currentStreak, maxStreak);
            }
            else{
                currentStreak = 0;
            }
        }
        return maxStreak;
    }
     
     /*
       Gets the maximum diagonal streak after the current move. It counts a maximum of 7 cells, 
       starting from the cell 3 positions away on the diagonal from its bottom-left direction 
       to the cell 3 positions away on the diagonal from the top-right direction. 
       (if out of bound then go to cell at the boundary of that direction)
    */
    private int getDiagonalStreak(int col, int row, char color){
        int currentStreak = 0;
        int maxStreak = 1;
        int minDistanceToRightAndTop = Math.min(3, Math.min(ColumnNum - col - 1, row));
        int minDistanceToLeftAndBottom = Math.min(3, Math.min(col, RowNum - row - 1));
        int currentCol = col - minDistanceToLeftAndBottom;
        int currentRow = row + minDistanceToLeftAndBottom;
        while(currentCol <= col + minDistanceToRightAndTop && currentRow >= row - minDistanceToRightAndTop){
            if(color == board[currentRow][currentCol]){
                currentStreak++;
                maxStreak = Math.max(currentStreak, maxStreak);
            }
            else{
                currentStreak = 0;
            }
            currentCol++;
            currentRow--;
        }
        return maxStreak;
    }

    /*
      Gets the maximum antidiagonal streak after the current move. It counts a maximum of 7 cells, 
      starting from the cell 3 positions away on the antidiagonal from its top-left direction 
      to the cell 3 positions away on the diagonal from the bottom-right direction.
      (if out of bound then go to the cell at the boundary of that direction)
    */
    private int getAntidiagonalStreak(int col, int row, char color){
        int currentStreak = 0;
        int maxStreak = 1;
        int minDistanceToRightAndBottom = Math.min(3, Math.min(ColumnNum - col - 1, RowNum - row - 1));
        int minDistanceToLeftAndTop = Math.min(3, Math.min(col, row));
        int currentCol = col - minDistanceToLeftAndTop;
        int currentRow = row - minDistanceToLeftAndTop;
        while(currentCol <= col + minDistanceToRightAndBottom && currentRow <= row + minDistanceToRightAndBottom){
            if(color == board[currentRow][currentCol]){
                currentStreak++;
                maxStreak = Math.max(currentStreak, maxStreak);
            }
            else{
                currentStreak = 0;
            }
            currentCol++;
            currentRow++;
        }
        return maxStreak;
    }  
    
    /*
      Prints winning statement for human player if he/she wins, AI player otherwise
    */
    private void printWinningStatement(char color)
    {
        if(color == Colors[0]){
           System.out.println("You win!!\n");
           humanScore = 4;
        }
        else{
           System.out.println("Sorry.. AI wins\n");
           AIScore = 4;
        }
        restart();
    }
    
    /*
      Prints the score for human player if he/she is on the current move, AI otherwise
    */
    private void printScore(char color, int currentScore){
        if(color == Colors[0]){
           humanScore = Math.max(humanScore, currentScore);
        }
        else{
           AIScore = Math.max(AIScore, currentScore);
        }
        System.out.println("you have a score of " + humanScore + "\nAI has a score of " + AIScore + "\n");
    } 
}
