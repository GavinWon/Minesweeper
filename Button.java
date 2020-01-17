import javax.swing.*;
import java.awt.*;

public class Button extends JButton
{
    private int row;
    private int col;
    
    public Button(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    public Button(Icon i, int row, int col)
    {
        super(i);
        this.row = row;
        this.col = col;
    }
    
    public int getRow()
    {
        return row;
    }
    
    public int getCol()
    {
        return col;
    }
    

}
