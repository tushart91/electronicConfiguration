package mainPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Teacher extends JPanel implements Runnable{
	
	/*
	 * @author tushart12
	 */
	
	private static final long serialVersionUID = 1L;
	Translate trs = new Translate();
	private JButton prevButton = new JButton(trs.TranslateString("PREVIOUS"));
	private JButton pButton = new JButton(trs.TranslateString("PAUSE"));
	private JButton nextButton = new JButton(trs.TranslateString("NEXT"));
	int ovals[][] = {{79,209},{77,258},{123,258},{74,308},{121,308},{73,356},{170,308},{121,356},{70,403},
    {171,356},{120,403},{69,450},{218,356},{167,403},{118,450},{69,496},{217,403},{164,450},{116,496}};
	Thread t = null;
	boolean threadSuspended;
	JFormattedTextField text1 = new JFormattedTextField();
	JFormattedTextField[] electronText = new JFormattedTextField[19];
	Image i0 = null;
	Image i1 = null;
	Image i2 = null;
	InputStream input0 = Teacher.class.getResourceAsStream("/mainPackage/fillingRule.gif");
	InputStream input1 = Teacher.class.getResourceAsStream("/mainPackage/error.png");
	InputStream input2 = Teacher.class.getResourceAsStream("/mainPackage/energyLevel.gif");
	JLabel imageLabel;
	JLabel errorImage;
	JLabel energyLevel;
	JButton submit = new JButton(trs.TranslateString("Done"));
	boolean pause;
	boolean error;
	boolean showAnswer;
	Integer time;
	Integer stage;
	Integer score;
	Integer attempted;
	int fontSize;
	int leftTitle;
	int count;
	int currentI;
	String fillingSubShellNames[] = {"1s","2s","2p","3s","3p","4s","3d","4p","5s","4d","5p","6s","4f",
									 "5d","6p","7s","5f","6d","7p"};
	String[] subShellNames = {"1s","2s","2p","3s","3p","3d","4s","4p","4d","4f","5s","5p","5d","5f",
							  "6s","6p","6d","7s","7p"};
	int[][] tempSubShellElectrons = {{0},{0,0},{0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0},{0,0}};
	Integer currentSubShellElectrons[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	Integer fillingSubShellElectrons[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int fillingElectrons[] = {2,2,6,2,6,2,10,6,2,10,6,2,14,10,6,2,14,10,6};
	int fillingSubShell[] =  {0,0,1,0,1,0, 2,1,0, 2,1,0, 3, 2,1,0, 3, 2,1};
	int fillingShell[] =     {1,2,2,3,3,4, 3,4,5, 4,5,6, 4, 5,6,7, 5, 6,7};
	int halfFilled[] = {24,42};
	int fullfilled[] = {29,47,79};
	Integer exceptions[] = {41,44,45,46,57,58,64,78,89,90,91,92,93,96};
	String res = "";
	String res1 = "";
	int stringCount;
	int selectedElement;
	
	public Teacher(){
			
		setSize(800,600);
		setLayout(null); 
		System.out.println("init(): begin");
		setBackground( Color.black );
		setPreferredSize(new Dimension(800, 600));
		setMaximumSize(this.getPreferredSize());
		setMinimumSize(this.getPreferredSize());
		for(int i=0;i<electronText.length;i++){	
			electronText[i] = new JFormattedTextField();			
			electronText[i].setForeground(Color.BLACK);
			electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
		}
		try{
			i0 = ImageIO.read(input0);
			i1 = ImageIO.read(input1);
			i2 = ImageIO.read(input2);
			imageLabel = new JLabel(new ImageIcon(i0));
			errorImage = new JLabel(new ImageIcon(i1));
			energyLevel = new JLabel(new ImageIcon(i2));
		}
		catch(Exception ee){
			System.out.println("Could Not Load Image");
		}
		
		/* Set Background of Components */
		prevButton.setBackground(Color.LIGHT_GRAY);
		pButton.setBackground(Color.LIGHT_GRAY);
		nextButton.setBackground(Color.LIGHT_GRAY);
		
		/* Set Bounds of all Components */
		prevButton.setBounds(220, 2, 120, 30);
		pButton.setBounds(340, 2, 120, 30);
		nextButton.setBounds(460, 2, 120, 30);
		imageLabel.setBounds(30, 190, 256, 386);
		text1.setBounds(520, 123, 50, 25);
		errorImage.setBounds(580, 127, 25, 20);
		energyLevel.setBounds(250, 275, 300, 300);
		submit.setBounds(465, 330, 120, 30);
		
		text1.setFont(new Font("Verdana",Font.BOLD,14));
		errorImage.setToolTipText(trs.TranslateString("Enter Valid Atomic Numbers"));
		for(int i=0;i<electronText.length;i++)
			electronText[i].setFont(new Font("Verdana",Font.BOLD,14));
		
		/* Set visibility of components */
		imageLabel.setVisible(false);
		text1.setVisible(false);
		errorImage.setVisible(false);
		submit.setVisible(false);
		energyLevel.setVisible(false);
		for(int i=0;i<electronText.length;i++)
			electronText[i].setVisible(false);
		
		/* Add all components */
		add(prevButton);
		add(pButton);
		add(nextButton);
		add(imageLabel);
		add(text1);
		add(errorImage);
		add(energyLevel);
		add(submit);
		for(int i=0;i<electronText.length;i++)
			add(electronText[i]);
		
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAnswer();
			}
		});
		
		/* Add ActionListener to the components */
		text1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(null, text1.getText());
				try{
					errorImage.setVisible(false);
					selectedElement = Integer.parseInt(text1.getText());
					if(stage==5){
						if(selectedElement>102){
							throw new Exception("Higher numbered elements do not have a stable configuration");
						}
						for(int i=0;i<exceptions.length;i++){
							if(exceptions[i]==selectedElement){
								throw new Exception("This element has an exceptional electronic configuration");
							}
						}
						for(int i=0;i<halfFilled.length;i++){
							if(halfFilled[i]==selectedElement){
								throw new Exception("This element has an exceptional electronic configuration");
							}
						}
						for(int i=0;i<fullfilled.length;i++){
							if(fullfilled[i]==selectedElement){
								throw new Exception("This element has an exceptional electronic configuration");
							}
						}
					}
					else if(stage==6){
						boolean flag = true;
						for(int i=0;i<halfFilled.length;i++){
							if(halfFilled[i]==selectedElement){
								flag = false;
								break;
							}
						}
						if(flag)
							throw new Exception("Only two elements follow the rule 24 & 42");
					}
				} catch(Exception ee){
					errorImage.setVisible(true);
					if(ee.getMessage().contains("For"))
						errorImage.setToolTipText(trs.TranslateString("Enter only numbers"));
					else
						errorImage.setToolTipText(ee.getMessage());
					selectedElement = 0;
				}
				finally{			
					time = 1;
					count = 0;
					repaint();
				}
			}
		});
		
		for(int i=0;i<electronText.length;i++)
			electronText[i].addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					repaint();
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					
					if( e.getKeyCode() == KeyEvent.VK_ENTER ){
						showAnswer();
					}
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					
				}
			});
		
		for(int i=0;i<electronText.length;i++){	
			electronText[i].addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					if(stage==7||stage==8){
						int temp;
						JFormattedTextField textField = (JFormattedTextField)e.getComponent();
						try{
							int j=0;
							for(;j<electronText.length;j++){
								if(electronText[j].equals((JFormattedTextField)e.getComponent()))
									break;
							}
							temp = Integer.parseInt(textField.getText());
							if(temp==currentSubShellElectrons[j]){
								textField.setBorder(BorderFactory.createLineBorder(Color.GREEN,2));
								repaint();
							}
							else{
								textField.setBorder(BorderFactory.createLineBorder(Color.RED,2));
								repaint();
							}
						}catch(Exception ee){
							textField.setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						}
					}
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					
					JFormattedTextField textField = (JFormattedTextField)e.getComponent();
					textField.setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
					
				}
			});
		}
		
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(stage==1){
					stage=2;
					fontSize=36;
					energyLevel.setVisible(true);
				}
				else if(stage==2){
					stage=3;
					time = 1;
					count = 0;
					stringCount = 0;
					selectedElement = 38;
					imageLabel.setVisible(true);
					energyLevel.setVisible(false);
				}
				else if(stage==3){
					stage=4;
					time = 1;
					count = 0;
					stringCount = 0;
					selectedElement = 24;
					computeConfiguration(24);
					imageLabel.setVisible(false);
				}
				else if(stage==4){
					stage = 5;
					time = 1;
					count = 0;
					selectedElement = 0;
					imageLabel.setVisible(false);
				}
				else if(stage==5){
					stage = 6;
					time = 1;
					count = 0;
					selectedElement = 0;
					errorImage.setVisible(false);
					imageLabel.setVisible(false);
				}
				else if(stage==6){
					stage = 7;
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(true);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					pButton.setText("NEW");
					for(int i=0;i<electronText.length;i++){
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
				}
				else if(stage==7){
					stage = 8;
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(true);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					for(int i=0;i<electronText.length;i++){
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
				}
				else if(stage==8){
					int t1 = JOptionPane.showConfirmDialog(null, trs.TranslateString("You are now entering Test Mode\nDo you want to proceed"),trs.TranslateString("Are You Sure?"),JOptionPane.YES_NO_OPTION);
					if(t1==JOptionPane.YES_OPTION){
						JOptionPane.showMessageDialog(null, trs.TranslateString("Best of Luck!"));
						stage = 9;
						time = 1;
						selectedElement = 0;
						imageLabel.setVisible(false);
						errorImage.setVisible(false);
						text1.setVisible(false);
						submit.setVisible(true);
						pButton.setText(trs.TranslateString("NEW"));
						submit.setEnabled(true);
						for(int i=0;i<electronText.length;i++){
							electronText[i].setEnabled(true);
							electronText[i].setForeground(Color.BLACK);
							electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						 	electronText[i].setText("");
						}
					}
				}
				else if(stage==9){
					stage=10;
					time = 1;
					imageLabel.setVisible(false);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(false);
					pButton.setText(trs.TranslateString("PAUSE"));
					for(int i=0;i<electronText.length;i++)
						electronText[i].setVisible(false);
				}
				
				repaint();
				
			}
		});
		
		prevButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(stage==2){
					stage=1;
					leftTitle = 430;
					fontSize = 1;
					energyLevel.setVisible(false);
				}
				else if(stage==3){
					stage=2;
					imageLabel.setVisible(false);
					energyLevel.setVisible(true);
				}
				else if(stage==4){
					stage=3;
					time = 1;
					count = 0;
					stringCount = 0;
					selectedElement = 38;
					imageLabel.setVisible(true);
				}
				else if(stage==5){
					stage=4;
					time = 1;
					count = 0;
					stringCount = 0;
					selectedElement = 24;
					computeConfiguration(24);
					imageLabel.setVisible(false);	
					text1.setVisible(false);
				}
				else if(stage==6){
					stage = 5;
					time = 1;
					count = 0;
					selectedElement = 0;
					errorImage.setVisible(false);
					imageLabel.setVisible(false);
				}
				else if(stage==7){
					stage = 6;
					time = 1;
					count = 0;
					selectedElement = 0;
					errorImage.setVisible(false);
					imageLabel.setVisible(false);
					submit.setVisible(false);
					text1.setVisible(true);
					pButton.setText(trs.TranslateString("PAUSE"));
					for(int i=0;i<electronText.length;i++)
						electronText[i].setVisible(false);
				}
				else if(stage==8){
					stage = 7;
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(false);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					pButton.setText(trs.TranslateString("NEW"));
					for(int i=0;i<electronText.length;i++){
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
				}
				else if(stage==9){
					stage = 8;
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(true);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					submit.setEnabled(true);
					for(int i=0;i<electronText.length;i++){
						electronText[i].setEnabled(true);
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
				}
				else if(stage==10){
					stage = 8;
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(true);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					submit.setEnabled(true);
					pButton.setText(trs.TranslateString("NEW"));
					for(int i=0;i<electronText.length;i++){
						electronText[i].setEnabled(true);
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
				}
				
				repaint();
				
			}
		});
		
		pButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(stage==7){
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(false);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					for(int i=0;i<electronText.length;i++){
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}					
					repaint();
				}
				else if(stage==9){
					time = 1;
					selectedElement = 0;
					imageLabel.setVisible(false);
					errorImage.setVisible(false);
					text1.setVisible(false);
					submit.setVisible(true);
					submit.setEnabled(true);
					for(int i=0;i<electronText.length;i++){
						electronText[i].setEnabled(true);
						electronText[i].setForeground(Color.BLACK);
						electronText[i].setBorder(BorderFactory.createLineBorder(new Color(122,138,145),1));
						electronText[i].setText("");
					}
					repaint();
				}
				else{
					if(!pause){
						pause = true;
						pButton.setText(trs.TranslateString("PLAY"));
					}
					else{
						pause = false;
						pButton.setText(trs.TranslateString("PAUSE"));
					}
				}
			}
		});
		
		/*
		 * Initialize Variables
		 */
		
		time = 1;
		stage = 1;
		leftTitle = 430;
		fontSize = 1;
		pause = false;
		count = 0;
		stringCount = 0;
		selectedElement = 0;
		error = false;
		score = 0;
		attempted = 0;
		
		System.out.println("init(): end");
        
        System.out.println("start(): begin");
        if ( t == null ) {
            System.out.println("start(): creating thread");
            t = new Thread( this );
            System.out.println("start(): starting thread");
            threadSuspended = false;
            t.start();
        }
        else {
            if ( threadSuspended ) {
                threadSuspended = false;
                System.out.println("start(): notifying thread");
                synchronized( this ) {
                notify();
                }
            }
        }
        System.out.println("start(): end");
			
	}
	
	@Override
	public void run(){
		
		System.out.println("run(): begin");
        try {
            while (true) {
            	
            	if(!pause){
            		if(stage==1 && fontSize<=36)
            		{
            			pButton.setEnabled(true);
            			prevButton.setEnabled(false);
            			fontSize++;
            			time++;
            			leftTitle-=7;
            			repaint();
            		}
            		else if(stage==2)
            		{            		
            			pButton.setEnabled(false);
            			prevButton.setEnabled(true);
            		}
            		else if(stage==3&&time<=1050)
            		{
            			if(time%100==0&&time>=200){
            				repaint();
            				computeConfiguration(38);
            				count++;
            			}
            			else if(time%50==0&&time>150){
            				selectedElement -= fillingElectrons[stringCount];
            				repaint();
            				stringCount++;
            			}
            			time++;
            			pButton.setEnabled(true);
            		}
            		else if(stage==4&&time<=600)
            		{
            			if(time==150)
            			{
            				computeConfiguration(24);
            				repaint();
            			}
            			else if(time==200||time==250||time==300||time==350||time==400||time==450||time==500){
            				repaint();
            				count++;
            				stringCount++;
            			}
            			else if(time==530||time==600)
            			{
            				repaint();
            			}
            			time++;
            			pButton.setEnabled(true);
            		}
            		else if(stage==5)
            		{
            			if(selectedElement!=0&&time==1){
            				time++;
            				computeConfiguration(selectedElement);
            			}
            			if((time==2||time%25==0)&&selectedElement>0){
            				repaint();
            				selectedElement -= fillingElectrons[count];
            				count++;
            				time++;
            			}
            			else if(selectedElement>0)
            				time++;
            		}
            		else if(stage==6){
            			if(selectedElement!=0&&time==1){
            				time++;
            				computeConfiguration(selectedElement);
            			}
            			if((time==2||time%25==0)&&selectedElement>0){
            				repaint();
            				selectedElement -= fillingElectrons[count];
            				count++;
            				time++;
            			}
            			else if(selectedElement>0)
            				time++;
            		}
            		else if(stage==7||stage==9){
            			pButton.setEnabled(true);
            		}
            		else
            		{
            			pButton.setEnabled(false);
            		}
            	}
            	// Now the thread checks to see if it should suspend itself
            	if ( threadSuspended ) {
            		synchronized( this ) {
            			while ( threadSuspended ) {
            				System.out.println("run(): waiting");
            				wait();
            			}
            		}
            	}
                System.out.println(time);
                System.out.println("run(): sleeping");
                Thread.sleep( 41 );  // interval given in milliseconds
            }
        }
        catch (InterruptedException e) { }
        System.out.println("run(): end");
		
	}
	
	@Override
	public void paint(Graphics g){
		System.out.println("paint()");
        g.clearRect(0, 0, 800, 600);
        switch(stage)
        {
            case 1:
                stageOne(g);
                break;
            case 2:
                stageTwo(g);
                break;
            case 3:
            	stageThree(g);
            	break;
            case 4:
            	stageFour(g);
            	break;
            case 5:
            	stageFive(g);
            	break;
            case 6:
            	stageSix(g);
            	break;
            case 7:
            	stageSeven(g);
            	break;
            case 8:
            	stageEight(g);
            	break;
            case 9:
            	stageNine(g);
            	break;
            case 10:
            	stageTen(g);
            	break;
        }
		
	}
	
	public void showAnswer(){
		
		if(stage==7){
			if(check())
				JOptionPane.showMessageDialog(null, trs.TranslateString("Correct. That was Good!\nHit Next or New"));						
			else
				JOptionPane.showMessageDialog(null, trs.TranslateString("Wrong"));
		}
		else if(stage==8){
			if(check())
				JOptionPane.showMessageDialog(null, trs.TranslateString("Correct. That was Good!\nHit Next"));						
			else
				JOptionPane.showMessageDialog(null, trs.TranslateString("Wrong"));
		}
		else if(stage==9){
			if(check()){
				JOptionPane.showMessageDialog(null, trs.TranslateString("Correct. That was Good!\nHit New or Next."));
				score += 1;
			}
			else{
				JOptionPane.showMessageDialog(null, trs.TranslateString("Wrong! The answer is shown now.\nHit New or Next."));
				showAnswer = true;
			}
			repaint();
			submit.setEnabled(false);
			for(int i=0;i<electronText.length;i++){
				electronText[i].setEnabled(false);
			}
		}
		
	}
	
	public boolean check(){
		boolean flag = true;
		int temp=0;
		for(int i=0;i<currentSubShellElectrons.length;i++){
			try{
				temp = Integer.parseInt(electronText[i].getText());
			}catch(Exception ee){ temp=0; }
			finally{
				if(currentSubShellElectrons[i]!=temp){
					flag=false;
					break;
				}
			}
		}
		return flag;
	}
	
	public void drawTranslatableString( Graphics g, String str , int x, int y)
    {
		String write = trs.TranslateString(str);
        g.drawString(write,x,y);
    }
	
	public void drawTranslatableDigit( Graphics g, Integer str , int x, int y)
    {
		String write = trs.TranslateDigit(str);
        g.drawString(write,x,y);
    }
	
	public void stageOne( Graphics g ){
		
		super.paint(g);
        g.setColor( Color.green );
        setBackground(Color.BLACK);
        g.setFont(new Font("Verdana", Font.PLAIN, fontSize));
        drawTranslatableString(g,"Electronic Configuration", leftTitle, 300);
        
	}
	

	
	public void stageTwo( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 24));
        drawTranslatableString(g,"What You Should Know", 260, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        
        drawTranslatableDigit(g, 1, 300, 150);
        drawTranslatableDigit(g, 2, 300, 180);
        drawTranslatableDigit(g, 3, 300, 210);
        drawTranslatableDigit(g, 4, 300, 240);
        drawTranslatableString(g,". Knowledge about Atoms",309,150);
        drawTranslatableString(g,". Knowledge about Shells",309,180);
        drawTranslatableString(g,". Knowledge about Subshells",309,210);
        drawTranslatableString(g,". The Energy Level Diagram",309,240);
        
        
	}
	
	public void stageThree( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 36));
        drawTranslatableString(g,"An Example", 280, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g,"Let us take an example to show how electrons are filled in the subshells.", 100, 130);
        drawTranslatableString(g, "Consider an atom of Strontium Z:", 100, 150);
        drawTranslatableDigit(g, 38, 365, 150);
        drawTranslatableString(g, "The order of filling electrons is shown in the diagram.",100,170);
        drawTranslatableString(g, "The electronic configuration of Strontium is computed",300,230);
        drawTranslatableString(g, "as follows:",300,250);
        
        if(selectedElement==0){
        	g.setColor(Color.RED);
        }
        g.drawRect(400,320,210,33);
        drawTranslatableString(g, "Remaining Electrons: ",410,342);
        drawTranslatableDigit(g, selectedElement, 575, 342);
        g.setColor(Color.BLACK);
        
        if(time>=200){
        	
        	res = "";
	        for(int i=0;i<count;i++)
	        	g.fillOval(ovals[i][0], ovals[i][1], 23, 23);
	        for(int i=0;i<stringCount;i++)
	        	res += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";
	        g.drawString(res,350,290);
	        
        }
	}
	
	public void computeConfiguration(int atomicNumber){
		
		int temp = atomicNumber;
		res = res1 = "";
		
		/*
			String fillingSubShellNames[] = {"1s","2s","2p","3s","3p","4s","3d","4p","5s","4d","5p","6s","4f",
		         							 "5d","6p","7s","5f","6d","7p"};
			String[] subShellNames = {"1s","2s","2p","3s","3p","3d","4s","4p","4d","4f","5s","5p","5d","5f",
									  "6s","6p","6d","7s","7p"};
			int[][] tempSubShellElectrons = {{0},{0,0},{0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0},{0,0}};
			int currentSubShellElectrons[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			int fillingSubShellElectrons[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			int fillingElectrons[] = {2,2,6,2,6,2,10,6,2,10,6,2,14,10,6,2,14,10,6};
			int fillingSubShell[] =  {0,0,1,0,1,0, 2,1,0, 2,1,0, 3, 2,1,0, 3, 2,1};
			int fillingShell[] =     {1,2,2,3,3,4, 3,4,5, 4,5,6, 4, 5,6,7, 5, 6,7};
			int halfFilled[] = {24,42};
			int fullfilled[] = {29,47,79};
			Integer exceptions[] = {41,44,45,46,57,58,64,78,89,90,91,92,93,96};
		 */
		
		for(int i=0;i<tempSubShellElectrons.length;i++)
		{
			for(int j=0;j<tempSubShellElectrons[i].length;j++)
				tempSubShellElectrons[i][j] = 0;
		}
		
		for(int i=0;i<fillingShell.length;i++){
			
			fillingSubShellElectrons[i] = 0;
			if(temp>fillingElectrons[i]){
				temp -= fillingElectrons[i];
				tempSubShellElectrons[fillingShell[i]-1][fillingSubShell[i]] = fillingElectrons[i];
				fillingSubShellElectrons[i] = fillingElectrons[i];
			}
			else{
				tempSubShellElectrons[fillingShell[i]-1][fillingSubShell[i]] = temp;
				fillingSubShellElectrons[i] = temp;
				temp = 0;
			}
			
		}
		
		if(atomicNumber==24){
			fillingSubShellElectrons[6] = tempSubShellElectrons[2][2] = 5;
			fillingSubShellElectrons[5] = tempSubShellElectrons[3][0] = 1;			
		}		
		else if(atomicNumber==42){
			fillingSubShellElectrons[9] = tempSubShellElectrons[3][2] = 5;
			fillingSubShellElectrons[8] = tempSubShellElectrons[4][0] = 1;
		}
		else if(atomicNumber==29){
			fillingSubShellElectrons[6] = tempSubShellElectrons[2][2] = 10;
			fillingSubShellElectrons[5] = tempSubShellElectrons[3][0] = 1;
		}
		else if(atomicNumber==47){
			fillingSubShellElectrons[9] = tempSubShellElectrons[3][2] = 10;
			fillingSubShellElectrons[8] = tempSubShellElectrons[4][0] = 1;
		}
		else if(atomicNumber==79){
			fillingSubShellElectrons[13] = tempSubShellElectrons[4][2] = 10;
			fillingSubShellElectrons[11] = tempSubShellElectrons[5][0] = 1;
		}
			
		for(int k=0,i=0;i<tempSubShellElectrons.length;i++){
			for(int j=0;j<tempSubShellElectrons[i].length;j++){
				currentSubShellElectrons[k] = tempSubShellElectrons[i][j];
				k++;
			}
		}
		
	}
	
	public void stageFour(Graphics g){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 36));
        drawTranslatableString(g,"The Exceptions", 260, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g, "Most elements are in accordance to the diagram shown before.", 50, 120);
        drawTranslatableString(g, "But some exceptions do exist. One of those exceptions is Chromium(24).", 50, 140);
        drawTranslatableString(g, "Let us construct the electronic configuration of Chromium from the previous diagram.", 50, 160);
        
        if(time>=150){
        	imageLabel.setVisible(true);
        }
        
        if(time>=200){
        	
        	res = "";
	        for(int i=0;i<count;i++)
	        	g.fillOval(ovals[i][0], ovals[i][1], 23, 23);
	        for(int i=0;i<count;i++)
	        {
	        	if(i==5)
	        		res += fillingSubShellNames[i]+2+" ";
	        	else if(i==6)
	        		res += fillingSubShellNames[i]+4+" ";
	        	else
	        	res += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";
	        }

	        if(time>=600)
	        {
	        	g.setColor(Color.RED);
		        g.fillOval(588, 179, 35, 35);
		        g.setColor(Color.BLUE);
		        g.fillOval(555, 179, 35, 35);
		        g.setColor(Color.BLACK);
		        g.drawArc(566, 175, 60, 180, 70, 40);
		        g.drawArc(566, 174, 60, 180, 70, 40);
		        g.drawLine(607, 180, 601, 171);
		        g.drawLine(607, 179, 598, 180);
		        g.fillOval(593, 168, 3, 3);
	        }
	        g.drawString(res,390,203);
	        
        }
        
        if(time>=530){
        	
        	drawTranslatableString(g, "However, this is not the right configuration.", 350, 230);
        	drawTranslatableString(g, "Due to the phenomenon of half filled electrons, the 3d orbitals", 300, 250);
        	drawTranslatableString(g, "that contain 4 electrons can acheive a stable state by", 300, 270);
        	drawTranslatableString(g, "obtaining 1 electron from the 4s orbital which is not at a very", 300, 290);
        	drawTranslatableString(g, "high energy level.", 300, 310);
        	
        }
        
        if(time>=600){
        	
        	drawTranslatableString(g, "Therefore the actual electron configuration of Chromium", 310, 340);
        	fillingSubShellElectrons[5] = 1;
        	fillingSubShellElectrons[6] = 5;
        	res = "";
        	for(int i=0;i<7;i++)
	        		res += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";
	        g.drawString(res,390,380);
	        g.setColor(Color.RED);
	        g.drawRect(380, 360, 255, 30);
	        g.drawRect(379, 359, 257, 32);
	        g.drawRect(378, 358, 259, 34);
        	
        }
        
	}
	
	public void stageFive( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Try It Yourself", 260, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g, "Enter the atomic number of an element that is not",80,130);
        drawTranslatableString(g, "an exception and hit enter:",80,150);
        text1.setVisible(true);
        text1.requestFocusInWindow();
        if(time==1&&selectedElement==0){
        	text1.setText("");
        }
        
        if(time>1){
        	imageLabel.setVisible(true);
        	
        	res = "";
        	res1 = "";
	        for(int i=0;i<count;i++)
	        	g.fillOval(ovals[i][0], ovals[i][1], 23, 23);
	        for(int i=0;i<count;i++){
	        	if(i<10)
	        		res += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";
	        	if(i>=10)
	        		res1 += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";	
	        }
	        g.drawString(res,340,223);
	        if(count>=10)
	        	g.drawString(res1, 350, 253);
	        if(selectedElement<=0){
	        	drawTranslatableString(g, "It is better to write in order of shell number", 340, 310);
	        	if(count>6){
		        	res = res1 = "";
		        	for(int i=0;i<currentSubShellElectrons.length;i++){
		        		if(currentSubShellElectrons[i]!=0){
		        			if(i<11)
		        				res += subShellNames[i]+currentSubShellElectrons[i]+" ";
		        			else
		        				res1 += subShellNames[i]+currentSubShellElectrons[i]+" "; 	        					
		        		}
		        	}
	        	
		        	g.drawString(res, 340, 340);
		        	if(res1.length()>1)
		        		g.drawString(res1, 350, 370);
	        	}
	        	
	        }
        	
        }
		
	}
	
	public void stageSix( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Try It Yourself", 260, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g, "Enter the atomic number of an element that follows",80,130);
        drawTranslatableString(g, "the half filled electron exception rule:",80,150);
        text1.setVisible(true);
        text1.requestFocusInWindow();
        if(time==1&&selectedElement==0)
        {
        	text1.setText("");
        }
        
        if(time>1){
        	imageLabel.setVisible(true);
        	
        	res = "";
        	res1 = "";
	        for(int i=0;i<count;i++)
	        	g.fillOval(ovals[i][0], ovals[i][1], 23, 23);
	        for(int i=0;i<count;i++)
	        {
	        	if(i<10)
	        		res += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";
	        	if(i>=10)
	        		res1 += fillingSubShellNames[i]+fillingSubShellElectrons[i]+" ";	
	        	
	        }
	        g.drawString(res,340,223);
	        if(count>=10)
	        	g.drawString(res1, 350, 253);
	        if(selectedElement<=0){
	        	drawTranslatableString(g, "It is better to write in order of shell number", 340, 310);
	        	if(count>6){
		        	res = res1 = "";
		        	for(int i=0;i<currentSubShellElectrons.length;i++){
		        		if(currentSubShellElectrons[i]!=0){
		        			if(i<11)
		        				res += subShellNames[i].toString()+currentSubShellElectrons[i].toString()+" ";
		        			else
		        				res1 += subShellNames[i]+currentSubShellElectrons[i]+" "; 	        					
		        		}
		        	}
	        	
		        	g.drawString(res, 340, 340);
		        	if(res1.length()>1)
		        		g.drawString(res1, 350, 370);
	        	}
	        }
        }
	}
	
	public void stageSeven( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Guided Mode", 280, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g, "Write the configuration of the atom using the Aufbau's principle",140,130);
        submit.setBounds(465, 330, 120, 30);
        boolean flag = false;
        while(!flag&&time==1){
        	selectedElement = 40 + (int)(Math.random()*102);
        	flag = true;
        	if(selectedElement>102)
        		flag=false;
        	if(flag){
	        	for(int i=0;i<exceptions.length;i++){
	        		if(exceptions[i]==selectedElement){
	        			flag=false;
	        		}
	        	}
        	}
        	if(flag){
        		for(int i=0;i<halfFilled.length;i++){
            		if(halfFilled[i]==selectedElement){
            			flag=false;
            		}
            	}
        	}
        	if(flag){
        		for(int i=0;i<fullfilled.length;i++){
            		if(fullfilled[i]==selectedElement){
            			flag=false;
            		}
            	}
        	}
        	if(flag)
        	{
        		computeConfiguration(selectedElement);
                imageLabel.setVisible(true);
                submit.setVisible(true);
        		time++;
        	}
        }
        drawTranslatableString(g,"Atomic Number: ",450,180);
        drawTranslatableDigit(g, selectedElement, 575, 180);
        
        for(int i=0;i<10;i++){
        	g.drawString(subShellNames[i],290+50*i,247);
        	electronText[i].setBounds(312+50*i, 230, 25, 25);
			electronText[i].setVisible(true);
        }
        for(int i=10;i<19;i++){
        	g.drawString(subShellNames[i],290+50*(i-10),287);
        	electronText[i].setBounds(312+50*(i-10), 270, 25, 25);
			electronText[i].setVisible(true);
        }
        int sum=0;
        for(int i=0;i<19;i++){
        	try{
        		sum += Integer.parseInt(electronText[i].getText());
        	}catch(Exception ee) {}
        }
        
        if(selectedElement-sum==0){
        	g.setColor(Color.RED);
        	g.drawRect(420,389,200,30);
        }
        g.setFont(new Font("Verdana",Font.BOLD,14));
        drawTranslatableString(g, "Remaining Electrons: ",430,410);
        drawTranslatableDigit(g, (selectedElement-sum), 595, 410);
        g.setFont(new Font("Verdana",Font.BOLD,12));
        drawTranslatableString(g, "Click the done button once finished or hit enter", 373, 500);
        g.setColor(Color.BLACK);
        drawTranslatableString(g, "Leave the textboxes not required blank", 400, 480);
	}
	
	public void stageEight( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Guided Mode", 280, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g,"Write the configuration of the atom using the Aufbau's principle",140,130);
        submit.setBounds(465, 330, 120, 30);
        while(time==1){
        	selectedElement = 42;
        	computeConfiguration(selectedElement);
        	time++;
        }
        drawTranslatableString(g,"Atomic Number: ",450,180);
        drawTranslatableDigit(g, selectedElement, 575, 180);
        for(int i=0;i<10;i++){
        	g.drawString(subShellNames[i],290+50*i,247);
        	electronText[i].setBounds(312+50*i, 230, 25, 25);
			electronText[i].setVisible(true);
        }
        for(int i=10;i<19;i++){
        	g.drawString(subShellNames[i],290+50*(i-10),287);
        	electronText[i].setBounds(312+50*(i-10), 270, 25, 25);
			electronText[i].setVisible(true);
        }
        int sum=0;
        for(int i=0;i<19;i++){
        	try{
        		sum += Integer.parseInt(electronText[i].getText());
        	}catch(Exception ee) {}
        }
        g.setFont(new Font("Verdana",Font.BOLD,12));
        if(selectedElement-sum==0){
        	g.setColor(Color.RED);
        	g.drawRect(420,389,200,30);
        }
        g.setFont(new Font("Verdana",Font.BOLD,14));
        drawTranslatableString(g, "Remaining Electrons: ",430,410);
        drawTranslatableDigit(g, (selectedElement-sum), 595, 410);
        g.setFont(new Font("Verdana",Font.BOLD,12));
        drawTranslatableString(g, "Click the done button once finished or the hit enter", 350, 540);
        drawTranslatableString(g, "Hint: The atom follows half-filled electron rule", 370, 480);
		g.setColor(Color.BLACK);
		drawTranslatableString(g, "Leave the textboxes not required blank", 400, 510);
	}
	
	public void stageNine( Graphics g ){
		
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Test Mode", 300, 80);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        drawTranslatableString(g, "Write the configuration of the atom using the Aufbau's principle",140,130);
        submit.setBounds(335, 330, 120, 30);
        boolean flag = false;
        while(!flag&&time==1){
        	selectedElement = 1 + (int)(Math.random()*102);
        	flag = true;
        	if(selectedElement>102)
        		flag=false;
        	if(flag){
	        	for(int i=0;i<exceptions.length;i++){
	        		if(exceptions[i]==selectedElement){
	        			flag=false;
	        		}
	        	}
        	}
        	if(flag){
        		for(int i=0;i<fullfilled.length;i++){
            		if(fullfilled[i]==selectedElement){
            			flag=false;
            		}
            	}
        	}
        	if(flag)
        	{
        		computeConfiguration(selectedElement);
        		attempted += 1;
                submit.setVisible(true);
        		time++;        		
        	}
        }
        
        drawTranslatableString(g,"Atomic Number: ",320,180);
        drawTranslatableDigit(g, selectedElement, 450, 180);
        for(int i=0;i<10;i++){
        	g.drawString(subShellNames[i],160+50*i,247);
        	electronText[i].setBounds(182+50*i, 230, 25, 25);
			electronText[i].setVisible(true);
        }
        for(int i=10;i<19;i++){
        	g.drawString(subShellNames[i],160+50*(i-10),287);
        	electronText[i].setBounds(182+50*(i-10), 270, 25, 25);
			electronText[i].setVisible(true);
        }
        g.setFont(new Font("Verdana",Font.BOLD,12));
        drawTranslatableString(g, "Click the done button once finished or hit enter", 220, 460);
        drawTranslatableString(g, "Leave the textboxes not required blank", 250, 430);
        drawTranslatableString(g, "Clicking Next Question without completing will count as wrong answer", 150, 400);
        drawTranslatableString(g, "Score:"+score+"/"+attempted, 600, 580);
        
        if(showAnswer){
        	for(int i=0;i<10;i++){
            	g.drawString(subShellNames[i],160+50*i,500);
            	g.drawString(currentSubShellElectrons[i].toString(),182+50*i, 500);
    			
            }
            submit.setBounds(335, 330, 120, 30);
            for(int i=10;i<19;i++){
            	g.drawString(subShellNames[i],160+50*(i-10),520);
            	g.drawString(currentSubShellElectrons[i].toString(),182+50*(i-10), 520);
            }
        }
        
	}
	
	public void stageTen( Graphics g ){
		setBackground(new Color(238,238,238));
		super.paint(g);
		g.setColor( Color.BLACK );
        g.setFont(new Font("Verdana", Font.PLAIN, 37));
        drawTranslatableString(g,"Thank You", 300, 160);
	}
	
}