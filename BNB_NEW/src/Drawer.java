import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class Drawer extends JFrame{
	private static final long serialVersionUID = 1L;
	public People people;
	public Computer[] computer;
	public boolean isStart = true;
	public static Map<String, Integer> map = new HashMap<String, Integer>();
	public int ComputerNum = 0;
	public int deathNum = 0;
			
	public Drawer(People people, Computer ... computer) {
		this.people = people;
		this.computer = computer;
		ComputerNum = computer.length;
		this.setUndecorated(true);
		this.setVisible(true);
		this.setLocation(350, 150);
		this.setLayout(null);
		this.setBackground(new Color(100, 100, 100, 100));
		this.setSize(Constant.width * Constant.Length, Constant.height * Constant.Length);
		this.addKeyListener(people);
	}
	
	public void init() {
		map.clear();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Font f = g.getFont();
		if(Constant.isRun) {
			Constant.gameTime = Constant.gameTime - Constant.SleepTime;
			if(Constant.gameTime <= 0) {
				people.state = People.State.LOSE;
				Constant.isRun = false;
			}
		}
		long time = System.currentTimeMillis();
		// ��ȡ�ᱬը�ĸ���
		if(Constant.isRun) {
			for(int i = 0; i < Constant.width; i++) {
				for(int j = 0; j < Constant.height; j++) {
					if(Math.abs(Constant.Map[j][i]) > 1 && time - Math.abs(Constant.Map[j][i]) >= Constant.BombTime) {
						if(!map.containsKey(i + "-" + j)) {
							map.putAll(Brain.PreBombing(new int[]{i, j}));
						}
					}
				}
		    }
		}
		for(int i = 0; i < Constant.width; i++) {
			for(int j = 0; j < Constant.height; j++) {
				if(Brain.queryBombing(i, j, map)) {
					// ���Ʊ�ը
					g.drawImage(Constant.I_Bombing, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
				} else {
					// ���Ƶ���
					if((i + j) % 2 == 0) {
						g.drawImage(Constant.I_Map1, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
					} else {
						g.drawImage(Constant.I_Map2, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
					}
					if(Constant.Map[j][i] > 0){
						// ��������
						if((time - Constant.Map[j][i]) % 600 > 300) {
							g.drawImage(Constant.I_Bomb, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
						} else {
							g.drawImage(Constant.I_Bomb2, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
						}
					} else if(Constant.Map[j][i] == -1) {
						// ���ƶ�ҩ
						g.drawImage(Constant.I_Du, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
					} else if(Constant.Map[j][i] < -1) {
						// ���ƶ�ҩ+����
						if((time - Math.abs(Constant.Map[j][i])) % 600 > 300) {
							g.drawImage(Constant.I_Bomb, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
						} else {
							g.drawImage(Constant.I_Bomb2, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
						}
						g.drawImage(Constant.I_Du, i * Constant.Length, j * Constant.Length, Constant.Length, Constant.Length, null);
					} 
				}
			}
	    }
		
		// �����Ƿ�����
		if(Brain.isOver(people, map)) {
			Constant.isRun = false;
			people.state = People.State.LOSE;
		}
		g.drawImage(people.shape, people.point.x, people.point.y, Constant.Length, Constant.Length, null);
		
		deathNum = 0;
		for(int i = 0; i < ComputerNum; i++) {
			g.setColor(Color.blue);
			if(computer[i].blood > 0) {
				// �����Ƿ�����
				if(Brain.isOver(computer[i], map)) {
					if(computer[i].invincible == 0) {
						computer[i].blood--;
						computer[i].invincible = Constant.Invincible;
					}
				}
				// ��ֹ������һ�α�ը��ͣ��ʱ������������³�����Ѫ�������Ϊ���ﱻը����޵�ʱ�����
				if(computer[i].invincible > 0) {
					computer[i].invincible--;
				}
				g.fill3DRect(computer[i].point.x, computer[i].point.y - 10, (Constant.Length / computer[i].fullBlood) * computer[i].blood, 8, false);
				g.setColor(Color.red);
				if(computer[i].blood < computer[i].fullBlood) {
					g.fill3DRect(computer[i].point.x + (Constant.Length / computer[i].fullBlood) * computer[i].blood, computer[i].point.y - 10, (Constant.Length / computer[i].fullBlood) * (computer[i].fullBlood - computer[i].blood), 8, false);
				}
				g.drawImage(computer[i].shape, computer[i].point.x, computer[i].point.y, Constant.Length, Constant.Length, null);
			} else {
				deathNum++;
			}
		}
		if(deathNum == ComputerNum) {
			people.state = People.State.WIN;
			Constant.isRun = false;
		}
		if(isStart) {
			g.drawImage(Constant.GameStart, 130, 80, null);
		}
		if(people.state == People.State.WIN) {
			g.setFont(new Font("WIN", Font.BOLD, 60));
			g.setColor(Color.BLUE);
			g.drawString("WIN!!!", 250, 270);
		} else if(people.state == People.State.LOSE) {
			g.setFont(new Font("LOSE", Font.BOLD, 60));
			g.setColor(Color.RED);
			g.drawString("LOSE!!!", 250, 270);
			
		}
		g.setColor(Color.black);
		g.setFont(f);
		g.drawString(String.valueOf("��Ϸʱ�䣺" + Math.floor(Constant.gameTime / 1000)), 556, 10);
		switch(Switch.level) {
		case 0: g.drawString(String.valueOf("�Ѷȣ���"), 580, 30);
		        break;
		case 1: g.drawString(String.valueOf("�Ѷȣ���ͨ"), 580, 30);
				break;
		case 2: g.drawString(String.valueOf("�Ѷȣ�����"), 580, 30);
				break;
		case 3: g.drawString(String.valueOf("�Ѷȣ���ʦ"), 580, 30);
				break;
		case 4: g.drawString(String.valueOf("�Ѷȣ���Ԩ"), 580, 30);
				break;
		}
		
	}
}
