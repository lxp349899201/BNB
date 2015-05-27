import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Switch extends Thread{
	public static Drawer drawer;
	public static People people;
	public static Computer computer;
	public static Computer computer2;
	public static int level = 0;
	AudioClip ac = null;
	
	public void levelInit() {
		switch(level) {
		case 0: computer2.blood = 0; 
				Constant.DuTime = Constant.easy;
				computer.duTime = Constant.DuTime;
		        break;
		case 1: computer.blood = 0; 
				Constant.DuTime = Constant.easy;
				computer.duTime = Constant.DuTime;
				Constant.gameTime *= 0.8;
				break;
		case 2: Constant.DuTime = Constant.easy;
				computer.duTime = Constant.DuTime;
				computer2.duTime = Constant.DuTime;
				Constant.gameTime *= 0.8;
				break;
		case 3: Constant.DuTime = Constant.hard;
				computer.duTime = Constant.DuTime;
				computer2.duTime = Constant.DuTime;
				Constant.gameTime *= 0.67;
				break;
		case 4: Constant.DuTime = Constant.crazy;
				computer.duTime = Constant.DuTime;
				computer2.duTime = Constant.DuTime;
				Constant.gameTime *= 0.67;
				break;
		}
	}
	
	public synchronized void init() {
		try {
			ac = Applet.newAudioClip(new URL("file:MUSIC"+File.separator+"gamestart.wav"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ac != null) ac.play();
		if(people == null) {
			people = new People(Constant.P_Blood);
		}
		if(computer == null) {
			computer = new Computer(Constant.C1, Constant.C_Speed1, Constant.C_Blood1);
			computer.setRole(people);
			computer.setShape(Constant.D_Computer, Constant.U_Computer, Constant.R_Computer, Constant.L_Computer);
		}
		if(computer2 == null) {
			computer2 = new Computer(Constant.C2, Constant.C_Speed2, Constant.C_Blood2);
			computer2.setRole(people);
			computer2.setShape(Constant.D_Boss, Constant.U_Boss, Constant.R_Boss, Constant.L_Boss);
		}
		if(drawer == null) {
			drawer = new Drawer(people, computer, computer2);
		}
		people.setDrawer(drawer);
		Brain.brain.setRole(people, computer, computer2);
		levelInit();
		Constant.initAudioClip();
		Constant.initPressBomb();
		Constant.initGet();
	}

	public void run() {
		init();
	
		// 开始游戏的动画和开始游戏的背景音乐将于2000毫秒后结束
		try {
			Thread.sleep(2400);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		drawer.isStart = false;
		System.gc();
		try {
			// 背景音乐
			ac = Applet.newAudioClip(new URL("file:MUSIC"+File.separator+"ppd.wav"));
			ac.loop();
			Brain.brain.callMoveThread();
			while(true) {
				try {
					Thread.sleep(Constant.DeathSleepTime);
					if(Constant.callRun) {
						people.init(Constant.P_Blood);
						computer.init(Constant.initC_Point1(), Constant.C_Speed1, Constant.C_Blood1);
						computer2.init(Constant.initC_Point2(), Constant.C_Speed2, Constant.C_Blood2);
						drawer.init();
						Constant.gameTime = Constant.initGameTime();
						Constant.Map = Constant.initMap();
						Constant.P_Speed = Constant.P_Normal;
						Constant.isRun = true;
						Constant.callRun = false;
						levelInit();
					}
					while(Constant.isRun) {
						try {
							Thread.sleep(Constant.SleepTime);
							drawer.repaint();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Switch s = new Switch(); 
		s.start();
	}
}
