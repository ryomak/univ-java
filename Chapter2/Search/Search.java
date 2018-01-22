import java.util.*;

public class Search {
	Node node[];
	Node goal;
	Node start;

	Search() {
		makeStateSpace();
	}

	private void makeStateSpace() {
		node = new Node[10];
		// 状態空間の生成
		node[0] = new Node("L.A.Airport", 0);
		start = node[0];
		node[1] = new Node("UCLA", 7);
		node[2] = new Node("Hollywood", 4);
		node[3] = new Node("Anaheim", 6);
		node[4] = new Node("Grand Canyon", 1);
		node[5] = new Node("SanDiego", 2);
		node[6] = new Node("Downtown", 3);
		node[7] = new Node("Pasadena", 4);
		node[8] = new Node("DisneyLand", 2);
		node[9] = new Node("Las Vegas", 0);
		goal = node[9];

		node[0].addChild(node[1], 1);
		node[0].addChild(node[2], 3);
		node[1].addChild(node[2], 1);
		node[1].addChild(node[6], 6);
		node[2].addChild(node[3], 6);
		node[2].addChild(node[6], 6);
		node[2].addChild(node[7], 3);
		node[3].addChild(node[4], 5);
		node[3].addChild(node[7], 2);
		node[3].addChild(node[8], 4);
		node[4].addChild(node[8], 2);
		node[4].addChild(node[9], 1);
		node[5].addChild(node[1], 1);
		node[6].addChild(node[5], 7);
		node[6].addChild(node[7], 2);
		node[7].addChild(node[8], 1);
		node[7].addChild(node[9], 7);
		node[8].addChild(node[9], 5);
	}

	/***
	 * 幅優先探索
	 */
	public void breadthFirst() {
		Vector open = new Vector();
		open.addElement(node[0]);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					Vector children = node.getChildren();
					// node を closed に入れる．
					closed.addElement(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							if (m == goal) {
								open.insertElementAt(m, 0);
							} else {
								open.addElement(m);
							}
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 深さ優先探索
	 */
	public void depthFirst() {
		Vector open = new Vector();
		open.addElement(node[0]);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					Vector children = node.getChildren();
					// node を closed に入れる．
					closed.addElement(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					// 以下を実行．幅優先探索と異なるのはこの部分である．
					// j は複数の子節点を適切にopenの先頭に置くために位置
					// を調整する変数であり，一般には展開したときの子節点
					// の位置は任意でかまわない．
					int j = 0;
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける
							m.setPointer(node);
							if (m == goal) {
								open.insertElementAt(m, 0);
							} else {
								open.insertElementAt(m, j);
							}
							j++;
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 分岐限定法
	 */
	public void branchAndBound() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					Vector children = node.getChildren();
					// node を closed に入れる．
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							// nodeまでの評価値とnode->mのコストを
							// 足したものをmの評価値とする
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
							open.addElement(m);
						}
						// 子節点mがopenに含まれているならば，
						if (open.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							if (gmn < m.getGValue()) {
								m.setGValue(gmn);
								m.setPointer(node);
							}
						}
					}
				}
			}
			open = sortUpperByGValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 山登り法
	 */
	public void hillClimbing() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;

		// Start を node とする．
		Node node = start;
		for (;;) {
			// node は ゴールか？
			if (node == goal) {
				success = true;
				break;
			} else {
				// node を展開して子節点をすべて求める．
				Vector children = node.getChildren();
				System.out.println(children.toString());
				for (int i = 0; i < children.size(); i++) {
					Node m = (Node) children.elementAt(i);
					// m から node へのポインタを付ける．
					m.setPointer(node);
				}
				// 子節点の中に goal があれば goal を node とする．
				// なければ，最小の hValue を持つ子節点 m を node
				// とする．
				boolean goalp = false;
				Node min = (Node) children.elementAt(0);
				for (int i = 0; i < children.size(); i++) {
					Node a = (Node) children.elementAt(i);
					if (a == goal) {
						goalp = true;
					} else if (min.getHValue() > a.getHValue()) {
						min = a;
					}
				}
				if (goalp) {
					node = goal;
				} else {
					node = min;
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 最良優先探索
	 */
	public void bestFirst() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					Vector children = node.getChildren();
					// node を closed に入れる．
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							open.addElement(m);
						}
					}
				}
			}
			open = sortUpperByHValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * A* アルゴリズム
	 */
	public void aStar() {
		Vector open = new Vector();
		open.addElement(start);
		start.setGValue(0);
		start.setFValue(0);
		Vector closed = new Vector();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = (Node) open.elementAt(0);
				open.removeElementAt(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					Vector children = node.getChildren();
					// node を closed に入れる．
					closed.addElement(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = (Node) children.elementAt(i);
						int gmn = node.getGValue() + node.getCost(m);
						int fmn = gmn + m.getHValue();

						// 各子節点mの評価値とポインタを設定する
						if (!open.contains(m) && !closed.contains(m)) {
							// 子節点mがopenにもclosedにも含まれていない場合
							// m から node へのポインタを付ける．
							m.setGValue(gmn);
							m.setFValue(fmn);
							m.setPointer(node);
							// mをopenに追加
							open.addElement(m);
						} else if (open.contains(m)) {
							// 子節点mがopenに含まれている場合
							if (gmn < m.getGValue()) {
								// 評価値を更新し，m から node へのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
							}
						} else if (closed.contains(m)) {
							if (gmn < m.getGValue()) {
								// 子節点mがclosedに含まれていて fmn < fm となる場合
								// 評価値を更新し，mからnodeへのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
								// 子節点mをclosedからopenに移動
								closed.removeElement(m);
								open.addElement(m);
							}
						}
					}
				}
			}
			open = sortUpperByFValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	
	
	/***
	 * 解の表示
	 */
	public void printSolution(Node theNode) {
		if (theNode == start) {
			System.out.println(theNode.toString());
		} else {
			System.out.print(theNode.toString() + " <- ");
			printSolution(theNode.getPointer());
		}
	}

	/***
	 * Vector を Node の fValue の昇順（小さい順）に列べ換える．
	 */
	public Vector sortUpperByFValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getFValue() > tmp.getFValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	
	/***
	 * Vector を Node の gValue の昇順（小さい順）に列べ換える．
	 */
	public Vector sortUpperByGValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getGValue() > tmp.getGValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	/***
	 * Vector を Node の hValue の昇順（小さい順）に列べ換える．
	 */
	public Vector sortUpperByHValue(Vector theOpen) {
		Vector newOpen = new Vector();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.elementAt(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.elementAt(i);
				if (min.getHValue() > tmp.getHValue()) {
					min = tmp;
				}
			}
			newOpen.addElement(min);
			theOpen.removeElement(min);
		}
		return newOpen;
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("USAGE:");
			System.out.println("java Search [Number]");
			System.out.println("[Number] = 1 : Bredth First Search");
			System.out.println("[Number] = 2 : Depth  First Search");
			System.out.println("[Number] = 3 : Branch and Bound Search");
			System.out.println("[Number] = 4 : Hill Climbing Search");
			System.out.println("[Number] = 5 : Best First Search");
			System.out.println("[Number] = 6 : A star Algorithm");
		} else {
			int which = Integer.parseInt(args[0]);
			switch (which) {
			case 1:
				// 幅優先探索
				System.out.println("\nBreadth First Search");
				(new Search()).breadthFirst();
				break;
			case 2:
				// 深さ優先探索
				System.out.println("\nDepth First Search");
				(new Search()).depthFirst();
				break;
			case 3:
				// 分岐限定法
				System.out.println("\nBranch and Bound Search");
				(new Search()).branchAndBound();
				break;
			case 4:
				// 山登り法
				System.out.println("\nHill Climbing Search");
				(new Search()).hillClimbing();
				break;
			case 5:
				// 最良優先探索
				System.out.println("\nBest First Search");
				(new Search()).bestFirst();
				break;
			case 6:
				// A*アルゴリズム
				System.out.println("\nA star Algorithm");
				(new Search()).aStar();
				break;
			default:
				System.out.println("Please input numbers 1 to 6");
			}
		}
	}
}

class Node {
	String name;
	Vector children;
	Hashtable childrenCosts;
	Node pointer; // 解表示のためのポインタ
	int gValue; // コスト
	int hValue; // ヒューリスティック値
	int fValue; // 評価値
	boolean hasGValue = false;
	boolean hasFValue = false;

	Node(String theName, int theHValue) {
		name = theName;
		children = new Vector();
		childrenCosts = new Hashtable();
		hValue = theHValue;
	}

	public String getName() {
		return name;
	}

	public void setPointer(Node theNode) {
		this.pointer = theNode;
	}

	public Node getPointer() {
		return this.pointer;
	}

	public int getGValue() {
		return gValue;
	}

	public void setGValue(int theGValue) {
		hasGValue = true;
		this.gValue = theGValue;
	}

	public int getHValue() {
		return hValue;
	}

	public int getFValue() {
		return fValue;
	}

	public void setFValue(int theFValue) {
		hasFValue = true;
		this.fValue = theFValue;
	}

	
	/***
	 * theChild この節点の子節点 theCost その子節点までのコスト
	 */
	public void addChild(Node theChild, int theCost) {
		children.addElement(theChild);
		childrenCosts.put(theChild, new Integer(theCost));
	}

	public Vector getChildren() {
		return children;
	}

	public int getCost(Node theChild) {
		return ((Integer) childrenCosts.get(theChild)).intValue();
	}

	public String toString() {
		String result = name + "(h:" + hValue + ")";
		if (hasGValue) {
			result = result + "(g:" + gValue + ")";
		}
		if (hasFValue) {
			result = result + "(f:" + fValue + ")";
		}
		return result;
	}
}
