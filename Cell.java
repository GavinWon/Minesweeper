
/**
 * 
 *  
 *
 *  @author  
 *  @version May 3, 2019
 *  @author  Period: TODO
 *  @author  Assignment: APCS_FinalProject_MineSweeper
 *
 *  @author  Sources: TODO
 */
public class Cell
{
    private String state;
    private int value;

    public Cell() {
        state = "Unclicked";
        value = 0;
    }
    
    public String getState()
    {
        return state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue( int value )
    {
        this.value = value;
    }
}
