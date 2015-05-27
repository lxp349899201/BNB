import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class Computer extends Role {
	Role role;
	enum DIR {U, D, L, R, LU, RU, LD, RD, C}; // 怪物位于人物的相对位置
	DIR dir = DIR.C;
	int cycle = Constant.Cycle;
	int invincible = 0;
	int duTime = Constant.DuTime;
	int speed = 0;
	int fullBlood = 0;
	static StringBuffer bombPoint = new StringBuffer();
	static int bombCount = Constant.C_BombCount;
	Image D_Computer;
	Image U_Computer;
	Image R_Computer;
	Image L_Computer;
	
	public Computer(Point p, int speed, int blood) {
		this.fullBlood = blood;
		this.speed = speed;
		this.blood = blood;
		point = p;
	}
	public void setShape(Image D, Image U, Image R, Image L) {
		D_Computer = D;
		U_Computer = U;
		R_Computer = R;
		L_Computer = L;
		shape = D_Computer;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	// 初始化，重新开始时调用
	public void init(Point p, int speed, int blood) {
		dir = DIR.C;
		cycle = Constant.Cycle;
		this.blood = blood;
		invincible = 0;
		duTime = Constant.DuTime;
		point = p;
		bombPoint.setLength(0);
		bombCount = Constant.C_BombCount;
	}
	// 移动方法
	public void move() {
		int[] p = Brain.getRolePoint(this.point);
		if(cycle == Constant.Cycle) {
			analyze();
			cycle = 0;
		} else {
			cycle++;
		}
		if(dir == DIR.U) {
			shape = D_Computer;
			if(Brain.isMoveAble(DIR.D, this)) {
				point.setLocation(point.x, point.y + speed > (Constant.height - 1) * Constant.Length ? (Constant.height - 1) * Constant.Length : (point.y + speed));
			}
		} else if(dir == DIR.D) {
			shape = U_Computer;
			if(Brain.isMoveAble(DIR.U, this)) {
				point.setLocation(point.x, point.y - speed < 0 ? 0 : (point.y - speed));
			}
		} else if(dir == DIR.L) {
			shape = R_Computer;
			if(Brain.isMoveAble(DIR.R, this)) {
				point.setLocation(point.x + speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + speed), point.y);
			} 
		} else if(dir == DIR.R) {
			shape = L_Computer;
			if(Brain.isMoveAble(DIR.L, this)) {
				point.setLocation(point.x - speed < 0 ? 0 : (point.x - speed), point.y);
			} 
		} else if(dir == DIR.RU) { 
			shape = D_Computer;
			if(Brain.isMoveAble(DIR.D, this)) {
				point.setLocation(point.x, point.y + speed > (Constant.height - 1) * Constant.Length ? (Constant.height - 1) * Constant.Length : (point.y + speed));
			} else {
				if(Brain.isMoveAble(DIR.L, this)) {
					shape = L_Computer;
					point.setLocation(point.x - speed < 0 ? 0 : (point.x - speed), point.y);
				} else if(Brain.isMoveAble(DIR.R, this)) {
					shape = R_Computer;
					point.setLocation(point.x + speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + speed), point.y);
				}
			}
		} else if(dir == DIR.LU) {
			shape = D_Computer;
			if(Brain.isMoveAble(DIR.D, this)) {
				point.setLocation(point.x, point.y + speed > (Constant.height - 1) * Constant.Length ? (Constant.height - 1) * Constant.Length : (point.y + speed));
			} else {
				if(Brain.isMoveAble(DIR.R, this)) {
					shape = R_Computer;
					point.setLocation(point.x + speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + speed), point.y);
				} else if(Brain.isMoveAble(DIR.L, this)) {
					shape = L_Computer;
					point.setLocation(point.x - speed < 0 ? 0 : (point.x - speed), point.y);
				}
			}
		} else if(dir == DIR.LD) {
			shape = U_Computer;
			if(Brain.isMoveAble(DIR.U, this)) {
				point.setLocation(point.x, point.y - speed < 0 ? 0 : (point.y - speed));
			} else {
				if(Brain.isMoveAble(DIR.R, this)) {
					shape = R_Computer;
					point.setLocation(point.x + speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + speed), point.y);
				} else if(Brain.isMoveAble(DIR.L, this)) {
					shape = L_Computer;
					point.setLocation(point.x - speed < 0 ? 0 : (point.x - speed), point.y);
				}
			}
		} else if(dir == DIR.RD) {
			shape = U_Computer;
			if(Brain.isMoveAble(DIR.U, this)) {
				point.setLocation(point.x, point.y - speed < 0 ? 0 : (point.y - speed));
			} else {
				if(Brain.isMoveAble(DIR.L, this)) {
					shape = L_Computer;
					point.setLocation(point.x - speed < 0 ? 0 : (point.x - speed), point.y);
				} else if(Brain.isMoveAble(DIR.R, this)) {
					shape = R_Computer;
					point.setLocation(point.x + speed > (Constant.width - 1) * Constant.Length ? (Constant.width - 1) * Constant.Length : (point.x + speed), point.y);
				} 
			}
		} else if(dir == DIR.C) {
			shape = D_Computer;
		}
		if(judgeReachBombArea() && !judgeIsBombAreaExsitsBomb()) {
			if(bombCount > 0) {
				if(Brain.setBomb(p)) {
					bombCount--;
					bombPoint.append("(" + p[0] + "-" + p[1] + ")");
				}
			}
		}
		if(duTime > 0) {
			duTime--;
		}
		if(!Brain.ifDuArea(p)) {
			if(duTime == 0) {
				Brain.setDu(p);
				duTime = Constant.DuTime;
			}
		}
	}
	/* 角色分析运动方向
	 * 
	 * */
	public void analyze() {
		int index = 0;
		ArrayList<DIR> dirs = judgeMove();
		dir = judgeDirector(Brain.getRolePoint(point), Brain.getRolePoint(role.point));
		int[] finalPoint = null;
		boolean isContinue = false;
		if(dir == DIR.LU || dir == DIR.RU) {
			dir = DIR.U;
		}
		if(dir == DIR.LD || dir == DIR.RD) {
			dir = DIR.D;
		}
		for(int i = 0; i < dirs.size(); i++) {
			if(dirs.get(i) == dir) {
				isContinue = true;
			}
		}
		if(!isContinue) {
			if(dirs.size() > 0) {
				index = (int) (Math.random() * dirs.size());
				dir = dirs.get(index);
			} else {
				finalPoint = judgeMove2();
				if(finalPoint == null) {
					dir = DIR.C;
				} else {
					dir = judgeDirector(Brain.getRolePoint(point), finalPoint);
				}
			}
		}
	}
	// 初步分析运动方向，判断站立处，已经相邻4个方位的安全性
	public ArrayList<DIR> judgeMove() {
		int[] p = Brain.getRolePoint(point);
		int p0_1 = (((p[0] - 1) < 0) ? 0 : (p[0] - 1));
		int p0$1 = (((p[0] + 1) >= Constant.width) ? Constant.width - 1 : (p[0] + 1));
		int p1_1 = (((p[1] - 1) < 0) ? 0 : (p[1] - 1));
		int p1$1 = (((p[1] + 1) >= Constant.height) ? Constant.height - 1 : (p[1] + 1));
		boolean isCurrentSafe = judgeSafe(p[0], p[1]);
		boolean isUpSafe;
		boolean isDownSafe;
		boolean isLeftSafe;
		boolean isRightSafe;
		if(p[0] == 0) {isLeftSafe = false;} else {isLeftSafe = judgeSafe(p0_1, p[1]);}
		if(p[1] == 0) {isUpSafe = false;} else {isUpSafe = judgeSafe(p[0], p1_1);}
		if(p[0] == Constant.width - 1) {isRightSafe = false;} else {isRightSafe = judgeSafe(p0$1, p[1]);}
		if(p[1] == Constant.height - 1) {isDownSafe = false;} else {isDownSafe = judgeSafe(p[0], p1$1);}
		ArrayList<DIR> arr = new ArrayList<DIR>();
		if(isCurrentSafe)  {arr.add(DIR.C);}
		if(isUpSafe)  {arr.add(DIR.D);}
		if(isLeftSafe)  {arr.add(DIR.R);}
		if(isDownSafe)  {arr.add(DIR.U);}
		if(isRightSafe)  {arr.add(DIR.L);}
		return arr;
	}
	// 当初步分析判断不安全时，进一步分析运动方向，对米字方向上的所有点进行判断，寻找安全落点。
	public int[] judgeMove2() {
		int[] p = Brain.getRolePoint(point);
		for(int i = 0; i < Constant.Power; i++) {
			for(int j = 0; j < Constant.Power; j++) {
				if((i == 0 && j == 0) || (i == 0 && j == 1) || (i == 1 && j == 0)) {
				} else if(i == 0 || j == 0) {
					if((p[0] + i) >= Constant.width || ((p[1] + j) >= Constant.height)) {
					} else {
						if(judgeSafe(p[0] + i,p[1] + j)) {
							return new int[]{p[0] + i,p[1] + j};
						}
					}
					if((p[0] - i) < 0 || (p[1] - j) < 0) {
					} else {
						if(judgeSafe(p[0] - i,p[1] - j)) {
							return new int[]{p[0] - i,p[1] - j};
						}
					}
				} else if(i == j) {
					if((p[0] + i)>= Constant.width || ((p[1] + j) >= Constant.height)) {
					} else {
						if(judgeSafe(p[0] + i,p[1] + j)) {
							return new int[]{p[0] + i,p[1] + j};
						}
					}
					if((p[0] - i) < 0 || (p[1] - j) < 0) { 
					} else {
						if(judgeSafe(p[0] - i,p[1] - j)) {
							return new int[]{p[0] - i,p[1] - j};
						}
					}
					if((p[0] + i)>= Constant.width || (p[1] - j) < 0) {
					} else {
						if(judgeSafe(p[0] + i,p[1] - j)) {
							return new int[]{p[0] + i,p[1] - j};
						}
					}
					if((p[0] - i) < 0 || ((p[1] + j) >= Constant.height)) {
					} else {
						if(judgeSafe(p[0] - i,p[1] + j)) {
							return new int[]{p[0] - i,p[1] + j};
						}
					}
				} else {
				}
			}
		}
		return null;
	}
	public DIR judgeDirector(int mySelfPoint[], int OtherRolePoint[]) {
		if(mySelfPoint[0] == OtherRolePoint[0] && mySelfPoint[1] > OtherRolePoint[1]) {
			// 怪物在人物的↓
			return DIR.D;
		} else if(mySelfPoint[0] == OtherRolePoint[0] && mySelfPoint[1] == OtherRolePoint[1]) {
			// 怪物和人物重叠
			return DIR.C;
		} else if(mySelfPoint[0] == OtherRolePoint[0] && mySelfPoint[1] < OtherRolePoint[1]) {
			// 怪物在人物的↑
			return DIR.U;
		} else if(mySelfPoint[0] > OtherRolePoint[0] && mySelfPoint[1] > OtherRolePoint[1]) {
			// 怪物在人物的↘
			return DIR.RD;
		} else if(mySelfPoint[0] > OtherRolePoint[0] && mySelfPoint[1] == OtherRolePoint[1]) {
			// 怪物在人物的→
			return DIR.R;
		} else if(mySelfPoint[0] > OtherRolePoint[0] && mySelfPoint[1] < OtherRolePoint[1]) {
			// 怪物在人物的↗
			return DIR.RU;
		} else if(mySelfPoint[0] < OtherRolePoint[0] && mySelfPoint[1] > OtherRolePoint[1]) {
			// 怪物在人物的↙
			return DIR.LD;
		} else if(mySelfPoint[0] < OtherRolePoint[0] && mySelfPoint[1] == OtherRolePoint[1]) {
			// 怪物在人物的←
			return DIR.L;
		} else {
			// 怪物在人物的↖
			return DIR.LU;
		}
	}
	// 人物是否进入怪物的攻击范围
	public boolean judgeReachBombArea() {
		int mySelfPoint[] = Brain.getRolePoint(this.point);
		int OtherRolePoint[] = Brain.getRolePoint(role.point);
		boolean inSight = false;
		if(Math.abs(mySelfPoint[0] - OtherRolePoint[0]) <= Constant.BombArea && Math.abs(mySelfPoint[1] - OtherRolePoint[1]) <= Constant.BombArea) {
			inSight = true;
		}
		return inSight;
	}
	// 攻击范围内炸弹数量
	public boolean judgeIsBombAreaExsitsBomb() {
		int mySelfPoint[] = Brain.getRolePoint(this.point);
		int i_s = mySelfPoint[1] - Constant.BombArea;
		int j_s = mySelfPoint[0] - Constant.BombArea;
		int i_e = mySelfPoint[1] + Constant.BombArea;
		int j_e = mySelfPoint[0] + Constant.BombArea;
		int count = 0;
		if(i_s < 0) i_s = 0;
		if(j_s < 0) j_s = 0;
		if(i_e > Constant.height - 1) i_e = Constant.height - 1;
		if(j_e > Constant.width - 1) j_e = Constant.width - 1;
		for(int i = i_s; i <= i_e; i++) {
			for(int j = j_s; j <= j_e; j++) {
				if(Math.abs(Constant.Map[i][j]) > 1) {
					count++;
					if(count > 1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	// 判断方格的安全
	public boolean judgeSafe(int point0, int point1) {
		if(Math.abs(Constant.Map[point1][point0]) > 1) {
			return false;
		}
		for(int i = 1; i < Constant.Power; i++) {
			if(point1 - i >= 0) {
				if(Math.abs(Constant.Map[point1 - i][point0]) > 1) {
					return false;
				}
			}
			if(point1 + i <  Constant.height) {
				if(Math.abs(Constant.Map[point1 + i][point0]) > 1) {
					return false;
				}
			}
			if(point0 - i >= 0) {
				if(Math.abs(Constant.Map[point1][point0 - i]) > 1) {
					return false;
				}
			}
			if(point0 + i <  Constant.width) {
				if(Math.abs(Constant.Map[point1][point0 + i]) > 1) {
					return false;
				}
			}
		}
		return true;
	}
}
