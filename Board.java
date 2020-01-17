import java.util.Random;


/**
 * 
 * 
 *
 * @author Gavin Wong, Tim Zou, Hanting Zhang
 * @version May 3, 2019
 * @author Period: TODO
 * @author Assignment: APCS_FinalProject_MineSweeper
 *
 * @author Sources: TODO
 */
public class Board
{
    private Cell[][] board;

    private int rows, cols;

    private int numMines, flags, uncoveredCells;


    /**
     * 
     * @param rows
     *            The number of rows
     * @param cols
     *            The number of columns
     * @param numMines
     *            The number of mines
     */
    public Board( int rows, int cols, int numMines )
    {
        board = new Cell[rows][cols];
        reset( rows, cols, numMines );
    }


    /**
     * 
     * Applies the rules of minesweeper and determines the outcome of a move.
     * Updates the board by calling helper methods.
     * 
     * @param row
     *            The row of the source
     * @param col
     *            The column of the source
     * @param leftClick
     *            Differentiates left and right clicks.
     * @return Returns a message about the game state which is to be displayed
     *         on the resetBtn from the MineSweeper class. "Reset" - Nothing
     *         occurs, the game continues. Otherwise, "You win!" and "Game
     *         over..." are self explanatory.
     */
    public String update( int row, int col, boolean leftClick )
    {
        String msg = "Reset"; // Default to "Reset".
        Cell c = board[row][col];

        if ( leftClick )
        {
            if ( c.getState().equals( "Unclicked" ) )
            {
                if ( c.getValue() == -1 )
                {
                    gameLost( row, col );
                    msg = "Game over...";
                }
                else
                {
                    reveal( row, col );
                }
            }
            else if ( c.getValue() > 0 )
            {
                msg = revealAllNeighbors( row, col );
            }
        }
        else // Right Click.
        {
            if ( c.getState().equals( "Flagged" ) )
            {
                c.setState( "Unclicked" );
                flags--;
            }
            else if ( c.getState().equals( "Unclicked" ) )
            {
                c.setState( "Flagged" );
                flags++;
            }

        }

        // Check for a win.
        if ( !msg.equals( "Game over..." )
            && uncoveredCells == rows * cols - numMines )
        {
            gameWon();
            return "You win!";
        }

        return msg; // Continue playing.
    }


    public String revealAllNeighbors( int row, int col )
    {
        System.out.println( "Revealing" );
        int i, j;
        int flagCount = 0;

        for ( i = -1; i < 2; i++ )
        {
            for ( j = -1; j < 2; j++ )
            {
                if ( isIn( row + i, col + j )
                    && board[row + i][col + j].getState()
                        .equals( "Flagged" ) )
                {
                    flagCount++;
                }
            }
        }

        System.out.println( "Flags: " + flagCount );
        if ( flagCount == board[row][col].getValue() )
        {
            for ( i = -1; i < 2; i++ )
            {
                for ( j = -1; j < 2; j++ )
                {
                    if ( isIn( row + i, col + j )
                        && board[row + i][col + j].getState()
                            .equals( "Unclicked" ) )
                    {
                        if ( board[row + i][col + j].getValue() == -1 )
                        {
                            gameLost( row + i, col + j );
                            return "Game over...";
                        }
                        else
                        {
                            reveal( row + i, col + j );
                        }
                    }
                }
            }
        }

        return "Reset";
    }


    /**
     * 
     * Helper method to uncover the result of making a move by floodfilling all
     * trivial cells (cells which have 0 neighboring mines).
     * 
     * @param row
     *            The row of the source
     * @param col
     *            The column of the source
     */
    private void reveal( int row, int col )
    {
        if ( row < 0 || row >= board.length || col >= board[0].length
            || col < 0 )
        {
            return;
        }

        Cell current = getCell( row, col );
        if ( !current.getState().equals( "Unclicked" ) )
        {
            return;
        }

        current.setState( "Clicked" );
        uncoveredCells++;

        if ( current.getValue() == 0 )
        {
            for ( int i = -1; i < 2; i++ )
            {
                for ( int j = -1; j < 2; j++ )
                {
                    // Floodfill by recursing.
                    reveal( row + i, col + j );
                }
            }
        }
    }


    /**
     * 
     * Helper method which modifies board to reveal all mine positions and
     * incorrect flags upon losing the game.
     * 
     * @param row
     *            The row of the source
     * @param col
     *            The column of the source
     */
    private void gameLost( int row, int col )
    {
        for ( int i = 0; i < rows; i++ )
        {
            for ( int j = 0; j < cols; j++ )
            {
                Cell c = board[i][j];
                if ( c.getValue() == -1
                    && c.getState().equals( "Unclicked" ) )
                {
                    c.setState( "Clicked" );
                }
                else if ( c.getState().equals( "Flagged" )
                    && c.getValue() != -1 )
                {
                    c.setState( "XBomb" );
                }
            }
        }

        board[row][col].setState( "Red" );
    }


    /**
     * 
     * Helper method which modifies board to flag all mines (which have not yet
     * been flagged) upon winning the game.
     */
    private void gameWon()
    {
        for ( int i = 0; i < rows; i++ )
        {
            for ( int j = 0; j < cols; j++ )
            {
                Cell c = board[i][j];
                if ( c.getValue() == -1 )
                {
                    c.setState( "Flagged" );
                }
            }
        }
    }


    /**
     * 
     * Resets the board. Randomly selects new mines and calculates the number of
     * neighboring mines for each cell, storing everything into a new board.
     * 
     * @param numMines
     * @param cols
     * @param rows
     * 
     * @param rows
     *            The number of rows
     * @param cols
     *            The number of column
     * @param numMines
     *            The number of mines
     */
    public void reset( int rows, int cols, int numMines )
    {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        uncoveredCells = 0;
        flags = 0;

        for ( int i = 0; i < board.length; i++ )
        {
            for ( int j = 0; j < board[0].length; j++ )
            {
                board[i][j] = new Cell();
            }
        }

        // Picking mines through reservoir sampling.
        // Sampling can only be done through an array, so the 2D positions are
        // mapped to a single value. i.e. (row, col) -> row * cols + col = n,
        // and visa-versa, n -> ( n // cols, n % cols ) = ( row, col ).

        int i;
        Random rand = new Random();
        int reservoir[] = new int[numMines];

        for ( i = 0; i < numMines; i++ )
            reservoir[i] = i;

        for ( ; i < rows * cols; i++ )
        {
            int j = rand.nextInt( i + 1 );

            if ( j < numMines )
                reservoir[j] = i;
        }

        for ( int n : reservoir )
        {
            int row = n / cols, col = n % cols;
            board[row][col].setValue( -1 );

            // Calculate the number of neighboring mines for each cell.
            for ( int ii = -1; ii < 2; ii++ )
            {
                for ( int jj = -1; jj < 2; jj++ )
                {
                    if ( isIn( row + ii, col + jj )
                        && board[row + ii][col + jj].getValue() != -1 )
                    {
                        board[row + ii][col + jj].setValue(
                            board[row + ii][col + jj].getValue() + 1 );
                    }
                }
            }
        }
    }


    /**
     * 
     ***** Helpers methods *****
     */

    public Cell getCell( int row, int col )
    {
        return board[row][col];
    }


    public Cell[][] getBoard()
    {
        return board;
    }


    public int getFlags()
    {
        return flags;
    }


    private boolean isIn( int row, int col )
    {
        return !( row < 0 || row >= rows || col >= cols || col < 0 );
    }
}
