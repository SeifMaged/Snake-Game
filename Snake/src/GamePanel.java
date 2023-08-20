import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75; // Game Speed
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6; // Snake size
	int applesEaten; // Score
	int highscore;
	int appleX; // Apple's X coordinate
	int appleY; // Apple's Y coordinate
	char direction = 'R'; // Snake movement direction 
	boolean running = false;
	Timer timer;
	Random random;
	File file;
	
	GamePanel(){
		
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
		
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		draw(g);
		
	}
	
	public void draw(Graphics g) {
		if(running) {
			// Displays grid(optional)
			for(int i = 0, grid = SCREEN_HEIGHT / UNIT_SIZE ; i < grid; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE , SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH , i * UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
		
			for(int i = 0; i < bodyParts; i++) {
			
				if(i == 0) { 
					// Draws snake head
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					// Draws snake body
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.WHITE);
			g.setFont(new Font("Showcard Gothic", Font.PLAIN, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
		} else
			try {
				gameOver(g);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	// Generates new apple at start of game or after previous apple has been eaten
	public void newApple() {
		
		boolean appleHidden;
		
		do {
		appleHidden = false;
		appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
		for(int i = bodyParts; i > 0; i--)
			if((appleX == x[i]) && (appleY == y[i])) {
				appleHidden = true;
				break;
			}
				
		} while(appleHidden);
	}
	
	public void move() {
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		switch(direction) {
		
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		// checks if head collides with body
		for(int i = bodyParts; i > 0; i--)
			if((x[0] == x[i]) && (y[0] == y[i]))
				running = false;
		
		//checks if head touches left border
		if(x[0] < 0) 
			running = false;
		
		//checks if head touches right border
		if(x[0] > SCREEN_WIDTH) 
			running = false;
		
		//checks if head touches top border
		if(y[0] < 0)
			running = false;
		
		//checks if head touches bottom border
		
		if(y[0] > SCREEN_HEIGHT)
			running = false;
		
		if(!running)
			timer.stop();
	}
	
	public void gameOver(Graphics g) throws IOException {
		
		file = new File("highscore");
		if(!file.exists()) {
			System.out.println("Error fetching previous Highscore");
		}
		else {
			
			FileReader reader = new FileReader(file);
			int data = reader.read() - 48;
			for(int i = 0; data != -49; i++) { 
				// loop exit condition is data != -49 since data = -1 when there is no more data to read
				// but 48 is subtracted from data to get number
				highscore = (int) (data + (highscore * Math.pow(10, i)));
				data = reader.read() - 48;
			}
			reader.close();
			
			if(applesEaten > highscore) {
				highscore = applesEaten;
				FileWriter writer = null;
				try {
					writer = new FileWriter(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					// update highscore then close writer
					writer.write(String.valueOf(highscore)); // must convert int to string representation
					writer.close();
				}
				
			}
				
		}
		//Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Showcard Gothic", Font.PLAIN, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
		
		// Score
		g.setColor(Color.red);
		g.setFont(new Font("Showcard Gothic", Font.PLAIN, 40));
		FontMetrics scoreMetrics = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
		
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Showcard Gothic", Font.PLAIN, 40));
		FontMetrics highscoreMetrics = getFontMetrics(g.getFont());
		g.drawString("Highscore: " + highscore, (SCREEN_WIDTH - highscoreMetrics.stringWidth("Highscore: " + highscore)) / 2, g.getFont().getSize() * 2);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R')
					direction = 'L';
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L')
					direction = 'R';
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D')
					direction = 'U';
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U')
					direction = 'D';
				break;
			}
			
		}
	}

}
