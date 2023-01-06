package com.lksmarcondes.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.lksmarcondes.entitie.BulletShoot;
import com.lksmarcondes.entitie.Enemy;
import com.lksmarcondes.entitie.Entity;
import com.lksmarcondes.entitie.Player;
import com.lksmarcondes.graficos.Spritesheet;
import com.lksmarcondes.graficos.UI;
import com.lksmarcondes.world.Camera;
import com.lksmarcondes.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {
	
	
	
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	public static Random rand;
	
	public UI ui;
	
	private BufferedImage image;
	
	public static ArrayList<Entity> entities;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	Menu menu;
	
	public static String gameState = "MENU";
	private boolean showGameOver = true;
	public int framesGameOver;
	
	private int CUR_LEVEL = 1;
	private int MAX_LEVEL = 3;
	public boolean restartGame = false;
	
	public boolean saveGame = false;
	
	public Game() {
		//Sound.musicBackground.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando obejtos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT , BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level"+CUR_LEVEL+".png");
		
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Zelda");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level", "vida"};
				int[] opt2 = {this.CUR_LEVEL, (int) player.life};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo!");
			}
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < bullets.size(); i++) {
				BulletShoot b = bullets.get(i);
				b.tick();
			}
			
			if(enemies.size() == 0){
				//Avançar para o próximo level
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					System.out.println("Parabéns, você ganhou");
					System.exit(1);
	 	  		}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showGameOver) {
					this.showGameOver = false;
				}else {
					this.showGameOver = true;
				}
			}
			
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				player.life = 100;
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {
			menu.tick();
		}
		
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(Color.gray);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		

		//Renderização do jogo
		world.render(g);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			BulletShoot b = bullets.get(i);
			b.render(g);
		}
		
		ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image,  0, 0, WIDTH*SCALE,HEIGHT*SCALE, null);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 580, 30);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g2.setFont(new Font("Calibri", Font.BOLD, 31));
			g2.setColor(Color.white);
			g2.drawString("GAME OVER!", 260, 250);
			g2.setFont(new Font("Calibri", Font.BOLD, 19));
			if(showGameOver) {
				g2.drawString("Pressione Enter para reiniciar", 230, 280);
			}
		}else if(gameState == "MENU"){
			menu.render(g);
		}
		bs.show();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
//		if(e.getKeyCode() == KeyEvent.VK_Z) {
//			player.jump = true;
//		}
		
		if((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = true;
		}else if((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) {
			player.left = true;
		}
		
		if((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
			player.up = true;
			if(gameState == "MENU") {
				menu.up = true;
			}
		}else if((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
			player.down = true;
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			//player.shoot = true;
			player.jump = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_L) {
			if(gameState == "NORMAL") {
				this.saveGame = true;
			}
			this.saveGame = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_P) {
			if(menu.pause == false) {
				gameState = "MENU";
				menu.pause = true;
			}else {
				gameState = "NORMAL";
				menu.pause = false;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = false;
		}else if((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) {
			player.left = false;
		}
		
		if((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
			player.up = false;
			if(gameState == "MENU") {
				menu.up = false;
			}
		}else if((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
			player.down = false;
			if(gameState == "MENU") {
				menu.down = false;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = false;
			if(gameState == "MENU") {
				menu.enter = false;
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}



