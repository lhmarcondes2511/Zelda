package com.lksmarcondes.entitie;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.lksmarcondes.graficos.Spritesheet;
import com.lksmarcondes.main.Game;
import com.lksmarcondes.world.Camera;
import com.lksmarcondes.world.World;

public class Player extends Entity{

	public boolean right, up, left, down;
	public int dir_right = 0, dir_left = 1;
	public int dir = dir_right;
	public double speed = 1.4;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	private boolean arma = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public static double life = 100;
	public double maxlife = 100;
	public int lifeFixed = 3;
	
	public int mx, my;
	
	public boolean shoot = false;
	public static int maxAmmo = 100;
	public boolean mouseShoot = false;
	
	public boolean jump = false;
	
	public boolean jumpUp, jumpDown = false;
	
	public int z = 0;
	
	public int jumpFrames = 50;
	public int jumpCur = 0;
	public int jumpSpd = 2;
	public boolean isJumping = false;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		for(int i = 0 ; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		
		for(int i = 0 ; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
		
	}
	
	public void tick(){
		
		if(jump) {
			if(isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if(isJumping == true) {
			if(jumpUp) {
				jumpCur+=jumpSpd;
			}else if(jumpDown) {
				jumpCur-=jumpSpd;
				if(jumpCur <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if(jumpCur >= jumpFrames) {
				jumpUp= false;
				jumpDown = true;
			}
		}
		
		moved = false;
		if(right && World.isFree((int)(x + speed), this.getY(), z)){
			moved = true;
			x+=speed;
			dir = dir_right;
		}else if(left && World.isFree((int)(x - speed), this.getY(), z)) {
			moved = true;
			x-=speed;
			dir = dir_left;
		}
		
		if(up && World.isFree(this.getX(), (int)(y - speed), z)){
			moved = true;
			y-=speed;
		}else if(down && World.isFree(this.getX(), (int)(y + speed), z)){
			moved = true;
			y+=speed;
		}
		
		if(moved == true) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkItemsLife();
		this.checkItemsAmmo();
		this.checkItemsGun();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 3) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			shoot = false;
			if(arma && ammo > 0) {
				ammo--;
				int dx = 0;
				int px = 0;
				int py = 8;
				if(dir == dir_right) {
					px = 16;
					dx = 1;
				}else{
					px = -8;
					dx = -1;
				}
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		

		if(mouseShoot) {
			mouseShoot = false;
			if(arma && ammo > 0) {
				ammo--;
				
				double angle = 0;
				
				int px = 8;
				int py = 8;
				if(dir == dir_right) {
					px = 16;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			//Game Over
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT * 16 - Game.HEIGHT);
		
	}
	
	public void checkItemsGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColliding(this,  e)) {
					arma = true;
					Game.entities.remove(e);
				}
			}
		}
	}
	
	public void checkItemsAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Bullet) {
				if(Entity.isColliding(this,  e)) {
					ammo += maxAmmo;
					if(ammo > maxAmmo){
						ammo = maxAmmo;
					}else {
						Game.entities.remove(i);
					}
					return;
				}
			}
		}
	}
	
	public void checkItemsLife() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Life) {
				if(Entity.isColliding(this,  e)) {
					life+=8;
					if(life >= 100) {
						life = 100;
					}else {
						Game.entities.remove(i);
					}
					return;
				}
			}
		}
	}
	
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == dir_right) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(arma) {
					//Desenhar para direita
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 4, this.getY() - Camera.y - z + 2, null);
				}
			}else if(dir == dir_left) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(arma) {
					//Desenhar para esquerda
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 5, this.getY() - Camera.y - z + 2, null);
				}
			}
		}else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
			if(dir == dir_right) {
				if(arma) {
					//Desenhar para direita
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 4, this.getY() - Camera.y - z + 2, null);
				}
			}else if(dir == dir_left) {
				if(arma) {
					//Desenhar para esquerda
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 5, this.getY() - Camera.y - z + 2, null);
				}
			}
		}
		
		
	}
	
}
