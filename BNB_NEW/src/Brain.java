import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Brain {
	public static Thread moveThread;
	public static Brain brain= new Brain();
	private Role[] role;
	private Brain() {};
	public void setRole(Role ... role) {
		this.role = role;
	}
	/* 获取角色站定的方格位置
	 * 判断角色正中心点落于Constant.Map的哪个方格中
	 * */
	public static int[] getRolePoint(Point point) {
		double x = (double)(point.x + Constant.Length / 2) /(double)Constant.Length;
		double y = (double)(point.y + Constant.Length / 2) /(double)Constant.Length;
		return new int[]{(int) Math.floor(x),  (int) Math.floor(y)};
	} 
	/* 获取某一点的位置
	 * 判断某点落于Constant.Map的哪个方格中*/
	public static boolean deathPoint(Point point, Map<String, Integer> map) {
		int x = (int) Math.floor((double)(point.x) / (double)Constant.Length);
		int y = (int) Math.floor((point.y) / (double)Constant.Length);
		if(x == Constant.width) x = Constant.width - 1;
		if(y == Constant.height) y = Constant.height - 1;
		if(map.containsKey(x + "-" + y)) {
			return true;
		} 
		return false;
	}
	/* 判断此方格中是否已经存在炸弹*/
	public static boolean ifBombExsit(int[] point) {
		boolean ifBombExsit = false;
		if(Math.abs(Constant.Map[point[1]][point[0]]) > 1) {
			ifBombExsit = true;
		}
		return ifBombExsit;
	}
	/* 给Constant.map的某个方格设置毒药*/
	public static void setDu(int[] point) {
		if(Constant.Map[point[1]][point[0]] == 0) {
			Constant.Map[point[1]][point[0]] = -1;
		} else if(Constant.Map[point[1]][point[0]] > 0) {
			Constant.Map[point[1]][point[0]] = -1 * Constant.Map[point[1]][point[0]];
		}
	}
	/* 判断此方格中是否已经存在毒药*/
	public static boolean ifDuArea(int[] point) {
		boolean ifDuArea = false;
		if(Constant.Map[point[1]][point[0]] < 0) {
			ifDuArea = true;
		}
		return ifDuArea;
	}
	/* 给Constant.map的某个方格设置炸弹*/
	public static boolean setBomb(int[] point) {
		synchronized (Constant.Map) {
			if(!Brain.ifBombExsit(point)) {
				long time = System.currentTimeMillis();
				if(Constant.Map[point[1]][point[0]] == -1) {
					Constant.Map[point[1]][point[0]] = (-1 * time);
				} else if(Constant.Map[point[1]][point[0]] == 0){
					Constant.Map[point[1]][point[0]] = time;
				}
				return true;
			} else {
				return false;
			}
		}
	}
	/*爆炸前的就绪工作，将由某方格内的炸弹引起的爆炸的方格存入一个MAP中*/
	public static Map<String, Integer> PreBombing(int[] point) {
		Map<String, Integer> pointMap = new HashMap<String, Integer>();
		findBomb(point[0], point[1], pointMap);
		return pointMap;
	}
	/* 爆炸中的查询工作，找出所有属于爆炸范围的炸弹*/
	public static void findBomb(int point0, int point1, Map<String, Integer> pointMap) {
		if(!pointMap.containsKey(point0 + "-" + (point1))) {
			pointMap.put(point0 + "-" + point1, Constant.BombCount);
		}
		for(int i = 1; i < Constant.Power; i++) {
			if(point1 - i >= 0) {
				if(!pointMap.containsKey(point0 + "-" + (point1 - i))) {
					pointMap.put(point0 + "-" + (point1 - i), Constant.BombCount);
					if(Math.abs(Constant.Map[point1 - i][point0]) > 1) {
						findBomb(point0, point1 - i, pointMap);
					}
				}
			} 
			if(point1 + i <  Constant.height) {
				if(!pointMap.containsKey(point0 + "-" + (point1 + i))) {
					pointMap.put(point0 + "-" + (point1 + i), Constant.BombCount);
					if(Math.abs(Constant.Map[point1 + i][point0]) > 1) {
						findBomb(point0, point1 + i, pointMap);
					}
				}
			} 
			if(point0 - i >= 0) {
				if(!pointMap.containsKey((point0 - i) + "-" + point1)) {
					pointMap.put((point0 - i) + "-" + point1, Constant.BombCount);
					if(Math.abs(Constant.Map[point1][point0 - i]) > 1) {
						findBomb(point0 - i, point1, pointMap);
					}
				}
			} 
			if(point0 + i <  Constant.width) {
				if(!pointMap.containsKey((point0 + i) + "-" + point1)) {
					pointMap.put((point0 + i) + "-" + point1, Constant.BombCount);
					if(Math.abs(Constant.Map[point1][point0 + i]) > 1) {
						findBomb(point0 + i, point1, pointMap);
					}
				}
			}
		}
	}
	/* 爆炸过程中的处理
	 * 爆炸过程是闪烁时间堆积而成的，总共闪烁Constant.BombCount次，针对于第一次和最后一次做判断。*/
	public static boolean queryBombing(int i, int j, Map<String, Integer> map) {
		boolean isBombOver = false;
		String temp = "";
		if(map.containsKey(i + "-" + j)) {
			int bombCount = map.get(i + "-" + j);
			isBombOver = true;
			if(bombCount >= 1 && Constant.isRun) {
				if(bombCount == Constant.BombCount) {
					Constant.bombSound.stop();
					Constant.bombSound.play();
				}
				bombCount = bombCount - 1;
				if(bombCount > 0) {
					map.put(i + "-" + j, bombCount);
				} else {
					if(People.bombPoint.indexOf("(" + i + "-" + j + ")") > -1) {
						temp = People.bombPoint.toString();
						temp = temp.replace("(" + i + "-" + j + ")", "");
						People.bombPoint.setLength(0);
						People.bombPoint.append(temp);
						People.bombCount++;
					}
					if(Computer.bombPoint.indexOf("(" + i + "-" + j + ")") > -1) {
						temp = Computer.bombPoint.toString();
						temp = temp.replace("(" + i + "-" + j + ")", "");
						Computer.bombPoint.setLength(0);
						Computer.bombPoint.append(temp);
						Computer.bombCount++;
					}
					map.remove(i + "-" + j);
					Constant.Map[j][i] = 0;
				}
			} 
		} else {
			isBombOver = false;
		}
		return isBombOver;
	}
	/* 判断游戏是否结束
	 * 角色拥有4条死亡线，其中平行的两个线跑进了爆炸范围，即视为死亡，这样是为了做出半身效果*/
	public static boolean isOver(Role r, Map<String, Integer> map) {
		if(r instanceof People) {
			int x1 = r.point.x;
			int y1 = r.point.y + Constant.deathPoint_Y1;
			int x2 = r.point.x + Constant.Length;
			int y2 = r.point.y + Constant.deathPoint_Y2;
			int x3 = r.point.x + Constant.deathPoint_X1;
			int y3 = r.point.y;
			int x4 = r.point.x + Constant.deathPoint_X2;
			int y4 = r.point.y + Constant.Length;
			Point p = new Point(x1, y1);
			boolean b1 = deathPoint(p, map);
			p.setLocation(x1, y2);
			boolean b2 = deathPoint(p, map);
			p.setLocation(x2, y2);
			boolean b3 = deathPoint(p, map);
			p.setLocation(x2, y1);
			boolean b4 = deathPoint(p, map);
			p.setLocation(x3, y3);
			boolean b5 = deathPoint(p, map);
			p.setLocation(x3, y4);
			boolean b6 = deathPoint(p, map);
			p.setLocation(x4, y3);
			boolean b7 = deathPoint(p, map);
			p.setLocation(x4, y4);
			boolean b8 = deathPoint(p, map);
			if((b1 && b2 && b3 && b4) || (b5 && b6 && b7 && b8)) {
				return true;
			}
			return false;
		} else {
			int[] p = getRolePoint(r.point);
			if(map.containsKey(p[0] + "-" + p[1])) {
				return true;
			}
			return false;
		}
	}
	public static boolean isMoveAble(Computer.DIR d, Role r) {
		int[] myPoint = getRolePoint(r.point);
		int[] nextPoint;
		int dir = 0;
		if(d == Computer.DIR.U) {
			if(myPoint[1] == 0) return true;
			nextPoint = new int[]{myPoint[0], myPoint[1] - 1};
			if(ifBombExsit(nextPoint)) {
				if(r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length) {
					if(r.point.y > myPoint[1] * Constant.Length) {
						dir = myPoint[1] * Constant.Length;
					} else {
						dir = r.point.y;
					}
					if(r.point.x >= myPoint[0] * Constant.Length - (Constant.Length / 2) && r.point.x < myPoint[0] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0] - 1, myPoint[1]};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.L_People;
								} else {
									r.shape = Constant.L_Computer;
								}
								r.point.setLocation(r.point.x - Constant.P_Speed, dir);
								return false;
							} else {
								r.point.setLocation(r.point.x, dir);
								return false;
							}
						} else {
							r.point.setLocation(r.point.x, dir);
							return false;
						}
					} else if(r.point.x < myPoint[0] * Constant.Length + (Constant.Length / 2) && r.point.x > myPoint[0] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0] + 1, myPoint[1]};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.R_People;
								} else {
									r.shape = Constant.R_Computer;
								}
								r.point.setLocation(r.point.x + Constant.P_Speed, dir);
								return false;
							} else {
								r.point.setLocation(r.point.x, dir);
								return false;
							}
						} else {
							r.point.setLocation(r.point.x, dir);
							return false;
						}
					} else {
						r.point.setLocation(r.point.x, dir);
						return false;
					}
				}
			} else {
				if(myPoint[0] == 0) {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x > myPoint[0] * Constant.Length && r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.L_People;
							} else {
								r.shape = Constant.L_Computer;
							}
							r.point.setLocation(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x - Constant.P_Speed, r.point.y);
							return false;
						}
					}
				} else if(myPoint[0] == Constant.width - 1) {
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x < myPoint[0] * Constant.Length && r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.R_People;
							} else {
								r.shape = Constant.R_Computer;
							}
							r.point.setLocation(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x + Constant.P_Speed, r.point.y);
							return false;
						}
					}
				} else {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x > myPoint[0] * Constant.Length && r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.L_People;
							} else {
								r.shape = Constant.L_Computer;
							}
							r.point.setLocation(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x - Constant.P_Speed, r.point.y);
							return false;
						}
					}
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x < myPoint[0] * Constant.Length && r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.R_People;
							} else {
								r.shape = Constant.R_Computer;
							}
							r.point.setLocation(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x + Constant.P_Speed, r.point.y);
							return false;
						}
					}
				}
			}
		} else if(d == Computer.DIR.D) {
			if(myPoint[1] == Constant.height - 1) return true;
			nextPoint = new int[]{myPoint[0], myPoint[1] + 1};
			if(ifBombExsit(nextPoint)) {
				if(r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length) {
					if(r.point.y < myPoint[1] * Constant.Length) {
						dir = myPoint[1] * Constant.Length;
					} else {
						dir = r.point.y;
					}
					if(r.point.x >= myPoint[0] * Constant.Length - (Constant.Length / 2) && r.point.x < myPoint[0] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0] - 1, myPoint[1]};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.L_People;
								} else {
									r.shape = Constant.L_Computer;
								}
								r.point.setLocation(r.point.x - Constant.P_Speed, dir);
								return false;
							} else {
								r.point.setLocation(r.point.x, dir);
								return false;
							}
						} else {
							r.point.setLocation(r.point.x, dir);
							return false;
						}
					} else if(r.point.x < myPoint[0] * Constant.Length + (Constant.Length / 2) && r.point.x > myPoint[0] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0] + 1, myPoint[1]};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.R_People;
								} else {
									r.shape = Constant.R_Computer;
								}
								r.point.setLocation(r.point.x + Constant.P_Speed, dir);
								return false;
							} else {
								r.point.setLocation(r.point.x, dir);
								return false;
							}
						} else {
							r.point.setLocation(r.point.x, dir);
							return false;
						}
					} else {
						r.point.setLocation(r.point.x, dir);
						return false;
					}
				}
			} else {
				if(myPoint[0] == 0) {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x > myPoint[0] * Constant.Length && r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.L_People;
							} else {
								r.shape = Constant.L_Computer;
							}
							r.point.setLocation(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x - Constant.P_Speed, r.point.y);
							return false;
						}
					}
				} else if(myPoint[0] == Constant.width - 1) {
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x < myPoint[0] * Constant.Length && r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.R_People;
							} else {
								r.shape = Constant.R_Computer;
							}
							r.point.setLocation(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x + Constant.P_Speed, r.point.y);
							return false;
						}
					}
				} else {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x > myPoint[0] * Constant.Length && r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.L_People;
							} else {
								r.shape = Constant.L_Computer;
							}
							r.point.setLocation(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x - Constant.P_Speed, r.point.y);
							return false;
						}
					}
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x < myPoint[0] * Constant.Length && r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.R_People;
							} else {
								r.shape = Constant.R_Computer;
							}
							r.point.setLocation(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length ? myPoint[0] * Constant.Length : r.point.x + Constant.P_Speed, r.point.y);
							return false;
						}
					}
				}
			}
		} else if(d == Computer.DIR.L) {
			if(myPoint[0] == 0) return true;
			nextPoint = new int[]{myPoint[0] - 1, myPoint[1]};
			if(ifBombExsit(nextPoint)) {
				if(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length) {
					if(r.point.x > myPoint[0] * Constant.Length) {
						dir = myPoint[0] * Constant.Length;
					} else {
						dir = r.point.x;
					}
					if(r.point.y >= myPoint[1] * Constant.Length - (Constant.Length / 2) && r.point.y < myPoint[1] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0], myPoint[1] - 1};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.U_People;
								} else {
									r.shape = Constant.U_Computer;
								}
								r.point.setLocation(dir, r.point.y - Constant.P_Speed);
								return false;
							} else {
								r.point.setLocation(dir, r.point.y);
								return false;
							}
						} else {
							r.point.setLocation(dir, r.point.y);
							return false;
						}
					} else if (r.point.y < myPoint[1] * Constant.Length + (Constant.Length / 2) && r.point.y > myPoint[1] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0], myPoint[1] + 1};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.D_People;
								} else {
									r.shape = Constant.D_Computer;
								}
								r.point.setLocation(dir, r.point.y + Constant.P_Speed);
								return false;
							} else {
								r.point.setLocation(dir, r.point.y);
								return false;
							}
						} else {
							r.point.setLocation(dir, r.point.y);
							return false;
						}
					} else {
						r.point.setLocation(dir, r.point.y);
						return false;
					}
				}
			} else {
				if(myPoint[1] == 0) {
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length && r.point.y > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.U_People;
							} else {
								r.shape = Constant.U_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y - Constant.P_Speed);
							return false;
						}
					}
				} else if(myPoint[1] == Constant.height - 1) {
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length && r.point.y < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.D_People;
							} else {
								r.shape = Constant.D_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y + Constant.P_Speed);
							return false;
						}
					}
				} else {
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length && r.point.y > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.U_People;
							} else {
								r.shape = Constant.U_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y - Constant.P_Speed);
							return false;
						}
					}
					nextPoint = new int[]{myPoint[0] - 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x - Constant.P_Speed < myPoint[0] * Constant.Length && r.point.y < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.D_People;
							} else {
								r.shape = Constant.D_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y + Constant.P_Speed);
							return false;
						}
					}
				}
			}
		} else if(d == Computer.DIR.R) {
			if(myPoint[0] == Constant.width - 1) return true;
			nextPoint = new int[]{myPoint[0] + 1, myPoint[1]};
			if(ifBombExsit(nextPoint)) {
				if(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length) {
					if(r.point.x < myPoint[0] * Constant.Length) {
						dir = myPoint[0] * Constant.Length;
					} else {
						dir = r.point.x;
					}
					if(r.point.y >= myPoint[1] * Constant.Length - (Constant.Length / 2) && r.point.y < myPoint[1] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0], myPoint[1] - 1};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.U_People;
								} else {
									r.shape = Constant.U_Computer;
								}
								r.point.setLocation(dir, r.point.y - Constant.P_Speed);
								return false;
							} else {
								r.point.setLocation(dir, r.point.y);
								return false;
							}
						} else {
							r.point.setLocation(dir, r.point.y);
							return false;
						}
					} else if (r.point.y < myPoint[1] * Constant.Length + (Constant.Length / 2) && r.point.y > myPoint[1] * Constant.Length) {
						nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
						if(!ifBombExsit(nextPoint)) {
							nextPoint = new int[]{myPoint[0], myPoint[1] + 1};
							if(!ifBombExsit(nextPoint)) {
								if(r instanceof People) {
									r.shape = Constant.D_People;
								} else {
									r.shape = Constant.D_Computer;
								}
								r.point.setLocation(dir, r.point.y + Constant.P_Speed);
								return false;
							} else {
								r.point.setLocation(dir, r.point.y);
								return false;
							}
						} else {
							r.point.setLocation(dir, r.point.y);
							return false;
						}
					} else {
						r.point.setLocation(dir, r.point.y);
						return false;
					}
				}
			} else {
				if(myPoint[1] == 0) {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length && r.point.y > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.U_People;
							} else {
								r.shape = Constant.U_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y - Constant.P_Speed);
							return false;
						}
					}
				} else if(myPoint[1] == Constant.height - 1) {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length && r.point.y < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.D_People;
							} else {
								r.shape = Constant.D_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y + Constant.P_Speed);
							return false;
						}
					}
				} else {
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] + 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length && r.point.y > myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.U_People;
							} else {
								r.shape = Constant.U_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y - Constant.P_Speed < myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y - Constant.P_Speed);
							return false;
						}
					}
					nextPoint = new int[]{myPoint[0] + 1, myPoint[1] - 1};
					if(ifBombExsit(nextPoint)) {
						if(r.point.x + Constant.P_Speed > myPoint[0] * Constant.Length && r.point.y < myPoint[1] * Constant.Length) {
							if(r instanceof People) {
								r.shape = Constant.D_People;
							} else {
								r.shape = Constant.D_Computer;
							}
							r.point.setLocation(r.point.x, r.point.y + Constant.P_Speed > myPoint[1] * Constant.Length ? myPoint[1] * Constant.Length : r.point.y + Constant.P_Speed);
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	// 角色推行的线程
	public synchronized void callMoveThread() {
		final int num = role.length;
		if(moveThread == null) {
			moveThread = new Thread("Move") {
				 			@Override
				 			public void run() {
				 				while(true) {
				 					try {
										Thread.sleep(Constant.DeathSleepTime);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
				 					while(Constant.isRun) {
				 						try {
				 							for(int i = 0; i < num; i++) {
				 								if(role[i].blood > 0) {
				 									role[i].move();
				 								}
				 							}
						 					Thread.sleep(Constant.SleepTime);
						 				} catch (InterruptedException e) {
						 					e.printStackTrace();
						 				}
					 				}
				 				}
				 			}
			             };
			moveThread.start();
		}
	}
}
