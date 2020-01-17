
/**
 * 
 * 
 *
 * @author Gavin Wong, Tim Zou, Hanting Zhang
 * @version May 3, 2019
 * @author Period: 5
 * @author Assignment: APCS_FinalProject_MineSweeper
 *
 * @author Sources: None
 */

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;


public class MineSweeper
{
    private Board board;

    private Button[][] buttonGrid;
    
    private JFrame f;
    private JPanel mainGrid;
    private JButton resetBtn, modeBtn;
    private JLabel mineTracker;

    private int numMines;
    
    private int dimension;


    /**
     * Creates a new board and a array which holds buttons. Initializes buttons
     * to their starting state.
     * @param d the dimension
     * @param n the number of mines
     */
    public MineSweeper(int d, int n)
    {
        dimension = d;
        numMines = n;

        board = new Board( dimension, dimension, numMines );
        buttonGrid = new Button[dimension][dimension];
        resetButtonGrid();
    }



    /**
     * 
     * Sets up and adds all game components to the given frame.
     * 
     * @param frame
     *            The given frame
     */
    public void createGameComponents( JFrame frame )
    {
        Container pane = frame.getContentPane();

        pane.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();

        mineTracker = new JLabel( "Remaining: " + numMines );
        mineTracker.setPreferredSize( new Dimension( 120, 25 ) );
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.33;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 10, 10, 5, 0 );
        pane.add( mineTracker, c );

        resetBtn = new JButton( "Reset" );
        resetBtn.setPreferredSize( new Dimension( 100, 25 ) ); 
        resetBtn.addMouseListener( new MouseAdapter()
        {

            /**
             * When clicked, reset the game.
             */
            @Override
            public void mouseReleased( MouseEvent e )
            {
                board.reset( dimension, dimension, numMines );
                mineTracker.setText( "Remaining: " + numMines );
                resetBtn.setText( "Reset" );
                resetButtonGrid();
            }
        } );

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.33;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets( 10, 0, 5, 0 );
        pane.add( resetBtn, c );

        modeBtn = new JButton( "Settings" );
        modeBtn.setPreferredSize( new Dimension( 100, 25 ) ); 
        modeBtn.addMouseListener( new MouseAdapter()
        {

            /**
             * When clicked, opens a settings window.
             * allows the user to put in the dimensions and the number of Mines
             * for a new MineSweeper game.
             */
            @Override
            public void mousePressed( MouseEvent e )
            {
                String dimensions = JOptionPane.showInputDialog( frame,
                    "Enter grid dimension" );
                String mines = JOptionPane.showInputDialog( frame,
                                "Enter the number of mines" );
                try
                {
                    dimension = Integer.parseInt( dimensions );
                }
                catch ( NumberFormatException error )
                {
                    JOptionPane.showMessageDialog( frame,
                        "Dimension invalid, must be an integer" );
                }
                try
                {
                    numMines = Integer.parseInt( mines );
                }
                catch ( NumberFormatException error )
                {
                    JOptionPane.showMessageDialog( frame,
                        "Number of mines invalid, must be an integer" );
                }
                if (numMines > dimension * dimension)
                {
                    JOptionPane.showMessageDialog( frame,
                        "The number of mines must be less than or "
                        + "equal to the board size" );
                }
                else if (numMines < 0)
                {
                    JOptionPane.showMessageDialog( frame,
                        "The number of mines needs to be greater than 0" );
                }
                else if (dimension < 1)
                {
                    JOptionPane.showMessageDialog( frame, 
                        "The dimension needs to be greater than 1" );
                } 
                else
                {
                      f.dispose();
                      MineSweeper m = new MineSweeper( dimension, numMines );
                      m.createAndShowGUI();

                }
            }
        } );

        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.33;
        c.weighty = 1.0;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets( 10, 0, 5, 10 );
        pane.add( modeBtn, c );

        mainGrid = new JPanel( new GridLayout( dimension, dimension ) );
        mainGrid.setBorder( BorderFactory.createLoweredBevelBorder() );

        for ( int i = 0; i < dimension; i++ )
        {
            for ( int j = 0; j < dimension; j++ )
            {
                Button btn = buttonGrid[i][j];
                btn.addMouseListener( new MouseAdapter()
                {
                    /**
                     * When clicked, update the board and the buttonGrid
                     * accordingly.
                     */
                    @Override
                    public void mousePressed( MouseEvent e )
                    {
                        // "Reset" is the default text in the resetBtn. If the
                        // text is not "Reset", then the game has ended and the
                        // buttons in buttonGrid will take no more input.
                        if ( !resetBtn.getText().equals( "Reset" ) )
                        {
                            return;
                        }

                        String msg = board.update( btn.getRow(),
                            btn.getCol(),
                            SwingUtilities.isLeftMouseButton( e ) );

                        updateButtonGrid();

                        mineTracker.setText( "Remaining: "
                            + ( numMines - board.getFlags() ) );
                        resetBtn.setText( msg );
                    }
                } );
                mainGrid.add( btn );
            }
        }

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 2;
        c.insets = new Insets( 5, 10, 10, 10 );
        pane.add( mainGrid, c );
    }


    /**
     * 
     * Iterates through board and updates each button in buttonGrid to the
     * corresponding icon.
     */
    public void updateButtonGrid()
    {
        ImageIcon imgIcon;

        System.out.println(buttonGrid[0][0].getSize());
        for ( int i = 0; i < dimension; i++ )
        {
            for ( int j = 0; j < dimension; j++ )
            {
                Cell c = board.getCell( i, j );

                // Numbers, mines, and blanks take priority, regardless if the
                // current cell is unclicked or flagged.
                imgIcon = new ImageIcon( c.getValue() + ".png" );

                // In the case that the current cell should be unclicked or
                // flagged, replace the icon acorrdingly.
                if ( !c.getState().equals( "Clicked" )
                    && !c.getState().equals( "Red" ) )
                {
                    imgIcon = new ImageIcon( c.getState() + ".png" );
                }

                // When the game is lost, there is one red tile which indicates
                // where the loss occured. This deals with that special case.
                if ( c.getState().equals( "Red" ) )
                {
                    System.out.println( "Here1" );
                    buttonGrid[i][j].setBackground( Color.RED );
                }

                Image img = imgIcon.getImage()
                    .getScaledInstance( 25, 25, Image.SCALE_DEFAULT );
                ImageIcon image2 = new ImageIcon( img );
                buttonGrid[i][j].setIcon( image2 );

            }
        }
    }


    /**
     * 
     * Resets every button in buttonGrid to an "Unclicked" state.
     */
    public void resetButtonGrid()
    {
        ImageIcon imgIcon;
        for ( int i = 0; i < dimension; i++ )
        {
            for ( int j = 0; j < dimension; j++ )
            {
                Button btn;
                if ( buttonGrid[i][j] == null )
                {
                    btn = new Button( i, j );
                    buttonGrid[i][j] = btn;
                }
                else
                {
                    btn = buttonGrid[i][j];
                }

                btn.setPreferredSize( new Dimension(25, 25) );
                btn.setBorderPainted( false );
                imgIcon = new ImageIcon( "Unclicked.png" );
                ImageIcon image = new ImageIcon( imgIcon.getImage()
                    .getScaledInstance( 25,
                        25,
                        Image.SCALE_DEFAULT ) );
                btn.setIcon( image );
                buttonGrid[i][j]
                    .setBackground( new Color( 180, 180, 180 ) );
            }
        }
    }


    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        // Create and set up the window.
        f = new JFrame( "MineSweeper" );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Set up the game.
        createGameComponents( f );

        // Display the window.
        f.setResizable( false );
        f.pack();
        f.setVisible( true );
    }


    public static void main( String[] args )
    {
        MineSweeper game = new MineSweeper(16, 10);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                game.createAndShowGUI();
            }
        } );
    }
}
