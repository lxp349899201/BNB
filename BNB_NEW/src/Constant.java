import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;


public class Constant {
	public static long[][] Map = initMap();
	public static int Length = 50;
	public static int width = 13;
	public static int height = 10;
	public static Image I_Map1 = new ImageIcon("PIC" + File.separator + "floor1.jpg").getImage();
	public static Image I_Map2 = new ImageIcon("PIC" + File.separator + "floor2.jpg").getImage();
	public static Image I_Bomb = new ImageIcon("PIC" + File.separator + "PP1.png").getImage();
	public static Image I_Bomb2 = new ImageIcon("PIC" + File.separator + "PP2.png").getImage();
	public static Image I_Du = new ImageIcon("PIC" + File.separator + "Du.png").getImage();
	public static Image I_Bombing = new ImageIcon("PIC" + File.separator + "Bombing.png").getImage();
	public static Image U_People = new ImageIcon("PIC" + File.separator + "up.png").getImage();
	public static Image D_People = new ImageIcon("PIC" + File.separator + "down.png").getImage();
	public static Image L_People = new ImageIcon("PIC" + File.separator + "left.png").getImage();
	public static Image R_People = new ImageIcon("PIC" + File.separator + "right.png").getImage();
	public static Image U_Computer = new ImageIcon("PIC" + File.separator + "computerup.png").getImage();
	public static Image D_Computer = new ImageIcon("PIC" + File.separator + "computerdown.png").getImage();
	public static Image L_Computer = new ImageIcon("PIC" + File.separator + "computerleft.png").getImage();
	public static Image R_Computer = new ImageIcon("PIC" + File.separator + "computerright.png").getImage();
	public static Image U_Boss = new ImageIcon("PIC" + File.separator + "bossup.png").getImage();
	public static Image D_Boss = new ImageIcon("PIC" + File.separator + "bossdown.png").getImage();
	public static Image L_Boss = new ImageIcon("PIC" + File.separator + "bossleft.png").getImage();
	public static Image R_Boss = new ImageIcon("PIC" + File.separator + "bossright.png").getImage();
	public static Image GameStart = new ImageIcon("PIC" + File.separator + "go.png").getImage();
	public static long SleepTime = 25;
	public static long DeathSleepTime = 25;
	public static Point P = initP_Point(); 
	public static Point C1 = initC_Point1();
	public static Point C2 = initC_Point2();
	public static int P_Speed = 7;
	public static int P_Slow = 2;
	public static int P_Normal = 7;
	public static int C_Speed1 = 8;
	public static int C_Speed2 = 10;
	public static int BombArea = 1;
	public static int Power = 6;
	public static int BombCount = 8;
	public static int BombTime = 4000;
	public static int Cycle = 1;
	public static int P_Blood = 1;
	public static int C_Blood1 = 4;
	public static int C_Blood2 = 4;
	public static int Invincible = 8;
	public static int P_BombCount = 6;
	public static int C_BombCount = 5;
	public static int Post_Du = 1200;
	public static int SlowTime = 200;
	public static int easy = 10000;
	public static int hard = 60;
	public static int crazy = 5;
	public static int DuTime = easy;
	public static boolean isRun = true;
	public static boolean callRun = false;
	public static int deathPoint_X1 = (Length / 2) - 8;
	public static int deathPoint_X2 = (Length / 2) + 5;
	public static int deathPoint_Y1 = Length - 15;
	public static int deathPoint_Y2 = Length - 10;
	public static long gameTime = initGameTime();
	public static AudioClip bombSound = null;
	public static AudioClip PressBomb = null;
	public static AudioClip get = null;
	public static long[][] initMap() {
	   return new long[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,0}};
	} 
	public static Point initP_Point() {
		return new Point((int)(Math.random() * 13) * 50, (int)(Math.random() * 5) * 50);
	}
	public static Point initC_Point1() {
		return new Point((int)(Math.random() * 6) * 50, ((int)(Math.random() * 5) + 5) * 50);
	}
	public static Point initC_Point2() {
		return new Point(((int)(Math.random() * 6) + 6) * 50, ((int)(Math.random() * 5) + 5) * 50);
	}
	public static long initGameTime() {
		return 150000;
	}
	public synchronized static void initAudioClip() {
		if(bombSound == null) {
			try {
				bombSound = Applet.newAudioClip(new URL("file:MUSIC"+File.separator+"bomb.wav"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized static void initPressBomb() {
		if(PressBomb == null) {
			try {
				PressBomb = Applet.newAudioClip(new URL("file:MUSIC"+File.separator+"lay.wav"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized static void initGet() {
		if(get == null) {
			try {
				get = Applet.newAudioClip(new URL("file:MUSIC"+File.separator+"get.wav"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
}
