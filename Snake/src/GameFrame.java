import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GameFrame extends JFrame{

	GameFrame(){
		
		
		this.add(new GamePanel());
		this.setTitle("Snake");
		ImageIcon image = new ImageIcon("snake.png"); // create ImageIcon
		this.setIconImage(image.getImage()); // change frame Icon
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // changes x to exit out of application instead of hide
		this.setResizable(false); 
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); // centers the JFrame to the screen
		
	}
}
