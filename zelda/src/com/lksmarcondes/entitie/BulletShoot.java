package com.lksmarcondes.entitie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.lksmarcondes.main.Game;
import com.lksmarcondes.world.Camera;

public class BulletShoot extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 15, curlife = 0;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x += dx * spd;
		y += dy * spd;
		if(curlife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
