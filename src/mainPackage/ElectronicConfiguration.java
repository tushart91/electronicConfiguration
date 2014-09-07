package mainPackage;

import javax.swing.JApplet;

public class ElectronicConfiguration extends JApplet{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Teacher panel = new Teacher();
    
    @Override
    public void init()
    {
        add(panel);
        setSize(800,600);
        setVisible(true);
    }
}