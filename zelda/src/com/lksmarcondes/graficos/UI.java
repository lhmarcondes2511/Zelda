package com.lksmarcondes.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.lksmarcondes.entitie.Player;
import com.lksmarcondes.main.Game;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 9)); 
		g.drawString("Your life", 2, 10);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 9));
		g.drawString((int)Game.player.life + " / " + (int)Game.player.maxlife, 45, 10);
		g.setColor(Color.RED);
		g.fillRect(8, 12, 50, 5);
		g.setColor(Color.GREEN);
		g.fillRect(8, 12, (int)((Game.player.life/Game.player.maxlife)* 50), 5);
		
		
	}
	
}
