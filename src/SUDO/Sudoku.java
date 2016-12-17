package SUDO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Myframe extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4753076496951651267L;
	public static Object obj = new Object();
	public final static JTextField[][] filed = new JTextField[9][9];

	public Myframe() {
		for (int a = 0; a < 9; a++) {
			for (int b = 0; b < 9; b++) {
				filed[a][b] = new JTextField();
				filed[a][b].setText("");
			}
		}
		JPanel jpan = new JPanel();
		jpan.setLayout(new GridLayout(9, 9));
		for (int a = 8; a > -1; a--) {
			for (int b = 0; b < 9; b++) {
				jpan.add(filed[b][a]);
			}
		}
		add(jpan, BorderLayout.CENTER);
		JPanel jpb = new JPanel();
		JButton button1 = new JButton("calc");
		JButton button2 = new JButton("close");
		jpb.add(button1);
		jpb.add(button2);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				synchronized (obj) {
					for (int a = 0; a < 9; a++) {
						for (int b3 = 0; b3 < 9; b3++) {
							int pp = 0;
							if (!(filed[a][b3].getText().trim().equals(""))) {
								pp = Integer.parseInt(filed[a][b3].getText().trim());
								Calculate.b[a][b3] = pp;
							}
						}
					}
				}
				synchronized (obj) {
					new Thread(new Calculate()).start();
				}
			}
		});

		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		add(jpb, BorderLayout.SOUTH);
	}
}

public class Sudoku {
	public static void main(String[] args) {
		Myframe myf = new Myframe();
		myf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myf.setTitle("sudoku");
		myf.setSize(500, 500);
		myf.setVisible(true);
	}
}


class Calculate implements Runnable {
	/*
	 *  Calculate 类实现了 Runnable 接口，实现了多线程操作，在计算每格数据的时候可以提高效率。
	 *  java 中多线程实现 Runnable ： 
	 *  步骤：
	 *		1.定义实现Runnable接口
	 * 		2.覆盖Runnable接口中的run方法，将线程要运行的代码存放在run方法中。
	 *		3.通过Thread类建立线程对象。
	 * 		4.将Runnable接口的子类对象作为实际参数传递给Thread类的构造函数。
	 *		5.调用Thread类的start方法开启线程并调用Runnable接口子类run方法。
	 */
	public static boolean[][] boo = new boolean[9][9];  //二维数组 boo 用于判断该格是否为空，如果已经填入了数值，就不用再填了。
	public static int upRow = 0;                       
	public static int upColumn = 0;
	public static int[][] b = new int[9][9];    //二维数据 b 将存储九宫格中的数据

	public static void flyBack(boolean[][] judge, int row, int column) {
		/*
		 * flyBack 函数用于查找没有填入数值的空格
		 */
		int s = column * 9 + row;
		s--;
		int quotient = s / 9;
		int remainder = s % 9;
		if (judge[remainder][quotient]) {
			flyBack(judge, remainder, quotient);
		} else {
			upRow = remainder;
			upColumn = quotient;
		}
	}

	public static void arrayAdd(ArrayList<Integer> array, TreeSet<Integer> tree) {
		/*
		 * arrayAdd 函数添加新的数值（1~9）到一行中，如果数据已经有了，跳过，没有就继续赋值。
		 * 由于数独的规则，每行每列每个小九宫格1~9不能重复，所以填写 arrayAdd() 函数来添加tree中没有的元素，如果有了就跳过。
		 */
		for (int z = 1; z < 10; z++) {
			boolean flag3 = true;
			Iterator<Integer> it = tree.iterator();
			while (it.hasNext()) {// 10
				int b = it.next().intValue();
				if (z == b) {
					flag3 = false;
					break;
				}
			}
			if (flag3) {
				array.add(new Integer(z));
			}
			flag3 = true;
		}
	}

	public static ArrayList<Integer> assume(int row, int column) {
		/*
		 * assume 函数将大九宫格分成9个小九宫格，主要是判断在同行同列同一个小九宫格内哪些数值已经被填充了，添加该格备选的数值，就是候选法的思想。
		 * 为了提高算法的效率，我们将大九宫格分成9个小九宫格
		 * 主要是分析在同行同列同一个小九宫格内哪些数值已经被填充了，然后地调用 arrayAdd() 函数，添加该格备选的数值。
		 */
		ArrayList<Integer> array = new ArrayList<Integer>();
		TreeSet<Integer> tree = new TreeSet<Integer>();
		if (0 <= row && row <= 2 && 0 <= column && column <= 2) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 0; a2 < 3; a2++) {
				for (int b4 = 0; b4 < 3; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (0 <= row && row <= 2 && 3 <= column && column <= 5) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 0; a2 < 3; a2++) {
				for (int b4 = 3; b4 < 6; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (0 <= row && row <= 2 && 6 <= column && column <= 8) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 0; a2 < 3; a2++) {
				for (int b4 = 6; b4 < 9; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (3 <= row && row <= 5 && 0 <= column && column <= 2) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 3; a2 < 6; a2++) {
				for (int b4 = 0; b4 < 3; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (3 <= row && row <= 5 && 3 <= column && column <= 5) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 3; a2 < 6; a2++) {
				for (int b4 = 3; b4 < 6; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (3 <= row && row <= 5 && 6 <= column && column <= 8) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 3; a2 < 6; a2++) {
				for (int b4 = 6; b4 < 9; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (6 <= row && row <= 8 && 0 <= column && column <= 2) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 6; a2 < 9; a2++) {
				for (int b4 = 0; b4 < 3; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (6 <= row && row <= 8 && 3 <= column && column <= 5) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 6; a2 < 9; a2++) {
				for (int b4 = 3; b4 < 6; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		} else if (6 <= row && row <= 8 && 6 <= column && column <= 8) {
			for (int a = 0; a < 9; a++) {
				if (a != column && b[row][a] != 0) {
					tree.add(new Integer(b[row][a]));
				}
			}
			for (int b1 = 0; b1 < 9; b1++) {
				if (b1 != row && b[b1][column] != 0) {
					tree.add(new Integer(b[b1][column]));
				}
			}
			for (int a2 = 6; a2 < 9; a2++) {
				for (int b4 = 6; b4 < 9; b4++) {
					if ((!(a2 == row && b4 == column)) && b[a2][b4] != 0) {
						tree.add(new Integer(b[a2][b4]));
					}
				}
			}
			arrayAdd(array, tree);
		}
		return array;
	}

	public void run() {
		/*
		 * run 函数开始运行整个程序，生成最后的结果。
		 * 在 run() 函数中填写空格的地方，我们的想法是将一行一行的分析，每个点都可能有几个值，
		 * 我们用一个数组 utilization 来存放所有可能的值，在这个值的基础上填写下一个空格，
		 * 当填写不动的时候回溯到这里，填写为 utilization 数组里的下一个值。
		 */
		for (int a = 0; a < 9; a++) {
			for (int b1 = 0; b1 < 9; b1++) {
				if (b[a][b1] != 0) {
					boo[a][b1] = true;
				} else {
					boo[a][b1] = false;
				}
			}
		}
		boolean flag = true;
		ArrayList<Integer>[][] utilization = new ArrayList[9][9];
		int row = 0;
		int column = 0;
		while (column < 9) {
			if (flag == true) {
				row = 0;
			}
			while (row < 9) {
				if (b[row][column] == 0) {
					if (flag) {
						ArrayList<Integer> list = assume(row, column);
						utilization[row][column] = list;
					}
					if (utilization[row][column].isEmpty()) {
						flyBack(boo, row, column);
						row = upRow;
						column = upColumn;
						b[row][column] = 0;
						column--;
						flag = false;
						break;
					} // if(list.isEmpty())
					else {
						b[row][column] = utilization[row][column].get(0);
						utilization[row][column].remove(0);
						flag = true;
						boolean r = true;
						for (int a1 = 0; a1 < 9; a1++) {
							for (int b1 = 0; b1 < 9; b1++) {
								if (r == false) {
									break;
								}
								if (b[a1][b1] == 0) {
									r = false;
								}
							}
						}
						if (r) {
							for (int a1 = 0; a1 < 9; a1++) {
								for (int b1 = 0; b1 < 9; b1++) {
									System.out.print("b[" + a1 + "][" + b1 + "]" + b[a1][b1] + ",");
									Myframe.filed[a1][b1].setText(b[a1][b1] + "");
								}
							}
						}
					}
				} // if(int[row][column]==0)
				else {
					flag = true;
				}
				row++;
			}
			column++;
		}
	}
}