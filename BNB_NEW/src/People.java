import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class People extends Role implements KeyListener {
	boolean move = false;
	int dir = 0;
	int slowTime = 0;
	static StringBuffer bombPoint = new StringBuffer();
	enum State {WIN, LOSE, OTHER};
	State state = State.OTHER;
	static int bombCount = Constant.P_BombCount;
	private Drawer drawer;
	
	public People(int blood) {
		this.blood = blood;
		point = Constant.P;
		shape = Constant.D_People;
	}
	public void init(int blood) {
		this.blood = blood;
		slowTime = 0;
		bombPoint.setLength(0);
		bombCount = Constant.P_BombCount;
		point = Constant.initP_Point();
		shape = Constant.D_People;
		move = false;
		dir = 0;
		state = State.OTHER;
	}

	// 按下键盘
	public void keyPressed (KeyEvent e) {
		int key = e.getKeyCode();
		if((key == KeyEvent.VK_UP) || (key == KeyEvent.VK_DOWN) || (key == KeyEvent.VK_LEFT) || (key == KeyEvent.VK_RIGHT)) {
			move = true;
			dir = key;
		}
	}
	// 释放键盘
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == dir) {
			move = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			int[] point = Brain.getRolePoint(this.point);
			if(bombCount > 0) {
				if(Brain.setBomb(point)) {
					bombCount--;
					bombPoint.append("(" + point[0] + "-" + point[1] + ")");
					Constant.PressBomb.stop();
					Constant.PressBomb.play();
				}
			}
		} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if(e.getKeyCode() == KeyEvent.VK_F5) {
			if(!Constant.isRun) {
				Constant.callRun = true;
			}
		} else if(e.getKeyCode() == KeyEvent.VK_F6) {
			if(!Constant.isRun) {
				drawer.repaint();
				Switch.level++;
				if(Switch.level > 4) Switch.level = 0;
			}
		}
	}
	
	public void move() {
		int [] p;
		if(slowTime > 0) {
			slowTime--;
		} else {
			Constant.P_Speed = Constant.P_Normal;
		}
		if(!move) return;
		if(dir == 0) return;
		if(dir == KeyEvent.VK_UP) {
			if(Brain.isMoveAble(Computer.DIR.U, this)) {
				point.setLocation(point.x, point.y - Constant.P_Speed < 0 ? 0 : (point.y - Constant.P_Speed));
			}
			shape = Constant.U_People;
		} else if(dir == KeyEvent.VK_DOWN) {
			if(Brain.isMoveAble(Computer.DIR.D, this)) {
				point.setLocation(point.x, point.y + Constant.P_Speed > (Constant.height - 1) * Constant.Length ? (Constant.height - 1) * Constant.Length : (point.y + Constant.P_Speed));
			}
			shape = Constant.D_People;
		} else if(dir == KeyEvent.VK_LEFT) {
			if(Brain.isMoveAble(Computer.DIR.L, this)) {
				point.setLocation(point.x - Constant.P_Speed < 0 ? 0 : (point.x - Constant.P_Speed), point.y);
			}
			shape = Constant.L_People;
		} else if(dir == KeyEvent.VK_RIGHT) {
			if(Brain.isMoveAble(Computer.DIR.R, this)) {
				point.setLocation(point.x + Constant.P_Speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + Constant.P_Speed), point.y);
			}
			shape = Constant.R_People;
		} 
		p = Brain.getRolePoint(point);
		// 中毒了
		if(Brain.ifDuArea(p)) {
			Constant.P_Speed = Constant.P_Slow;
			if(Constant.Map[p[1]][p[0]] == -1) {
				Constant.Map[p[1]][p[0]] = 0;
			} else if(Constant.Map[p[1]][p[0]] < -1) {
				Constant.Map[p[1]][p[0]] = Math.abs(Constant.Map[p[1]][p[0]]) ;
			}
			Constant.get.stop();
			Constant.get.play();
			slowTime = Constant.SlowTime;
		}
	}
	
	public void keyTyped(KeyEvent arg0) {}
	
	public void setDrawer(Drawer drawer) {
		this.drawer = drawer;
	}
}
