package com.lksmarcondes.entitie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.lksmarcondes.graficos.Spritesheet;
import com.lksmarcondes.main.Game;
import com.lksmarcondes.main.Sound;
import com.lksmarcondes.world.Camera;
import com.lksmarcondes.world.World;

public class Enemy extends Entity {

	private double speed = 0.6;
	
	private int maskx = 8;
	private int masky = 8;
	private int maskw = 10;
	private int maskh = 10;
	
	public int dir_right = 0, dir_left = 1;
	public int dir = dir_right;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	public boolean right, up, left, down;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	
	private int life = 20;
	
	private boolean isDamaged = false;
	private int damagedFrames = 5, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		rightEnemy = new BufferedImage[4];
		leftEnemy = new BufferedImage[4];

		
		for(int i = 0 ; i < 4; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite((i * 16) + 32, 32, 16, 16);
		}
		
		for(int i = 0 ; i < 4; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite((i * 16) + 32, 48, 16, 16);
		}
	}
	
	public void tick() {
			maskx = 8;
			masky = 8;
			maskw = 8;
			maskh = 8;
			moved = false;
			
			if(this.isCollidingWithPlayer() == false) {
				if((int)x < Game.player.getX() && World.isFree((int)(x + speed), this.getY(), z)
						&& !isColliding((int)(x + speed), this.getY())) {
					x+=speed;
					dir = dir_right;
					moved = true;
				}else if((int)x > Game.player.getX() && World.isFree((int)(x - speed), this.getY(), z)
						&& !isColliding((int)(x - speed), this.getY())) {
					x-=speed;
					dir = dir_left;
					moved = true;
				}
				
				if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed), z)
						&& !isColliding(this.getX(), (int)(y + speed))) {
					y+=speed;
				}else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed), z)
						&& !isColliding(this.getX(), (int)(y - speed))) {
					y-=speed;
				}
			}else {
				// Estamos perto do player
				if(Game.rand.nextInt(100) < 10) {
					//Sound.hurtEffect.play();
					Game.player.life-= Game.rand.nextInt(3);
					Game.player.isDamaged = true;
					if(Game.player.life <= 0) {
						// Game Over
						//System.exit(1);
					}
				}
				
			}
			
			if(moved) {
				frames++;
				if(frames == maxFrames) {
					frames = 0;
					index++;
					if(index > maxIndex) {
						index = 0;
					}
				}
			}
			
			collidingBullet();
			if(life < 0) {
				destroySelf();
				return;
			}

			
			if(isDamaged) {
				this.damageCurrent++;
				if(this.damageCurrent == this.damagedFrames) {
					this.damageCurrent = 0;
					this.isDamaged = false;
				}
			}
		
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColliding(this, e)) {
					isDamaged = true;
					life -= 5;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) {
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == dir_right) {
				g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}else if(dir == dir_left) {
				g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}

}
